package com.github.thomasdarimont.keycloak.embedded;

import com.github.thomasdarimont.keycloak.embedded.KeycloakCustomProperties.AdminUser;
import org.keycloak.Config;
import org.keycloak.common.util.Resteasy;
import org.keycloak.exportimport.ExportImportConfig;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

    static KeycloakProperties keycloakProperties;

    static KeycloakCustomProperties customProperties;

    static Config.ConfigProvider configProvider;

    public EmbeddedKeycloakApplication(@Context ServletContext context) {

        Resteasy.pushContext(ServletContext.class, augmentToRedirectContextPath(context));
        tryCreateMasterRealmAdminUser();
        tryImportRealm();
    }

    protected void loadConfig() {
        Config.init(configProvider);
    }

    private void tryCreateMasterRealmAdminUser() {

        KeycloakSession session = getSessionFactory().create();

        ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);

        AdminUser admin = customProperties.getAdminUser();

        try {
            session.getTransactionManager().begin();
            applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
            session.getTransactionManager().commit();
        } catch (Exception ex) {
            LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
            session.getTransactionManager().rollback();
        }

        session.close();
    }

    private void tryImportRealm() {

        KeycloakCustomProperties.Migration imex = customProperties.getMigration();
        Resource importLocation = imex.getImportLocation();

        if (!importLocation.exists()) {
            LOG.info("Could not find keycloak import file %s", importLocation);
            return;
        }

        File file;
        try {
            file = importLocation.getFile();
        } catch (IOException e) {
            LOG.error("Could not read keycloak import file %s", importLocation, e);
            return;
        }

        LOG.info("Starting Keycloak realm configuration import from location: %s", importLocation);

        KeycloakSession session = getSessionFactory().create();

        ExportImportConfig.setAction("import");
        ExportImportConfig.setProvider(imex.getImportProvider());
        ExportImportConfig.setFile(file.getAbsolutePath());

        ExportImportManager manager = new ExportImportManager(session);
        manager.runImport();

        session.close();

        LOG.info("Keycloak realm configuration import finished.");
    }


    private static ServletContext augmentToRedirectContextPath(ServletContext servletContext) {

        ClassLoader classLoader = servletContext.getClassLoader();
        Class<?>[] interfaces = {ServletContext.class};
        KeycloakCustomProperties.Server server = customProperties.getServer();

        InvocationHandler invocationHandler = (proxy, method, args) -> {

            if ("getContextPath".equals(method.getName())) {
                return server.getContextPath();
            }

            if ("getInitParameter".equals(method.getName()) && args.length == 1 && "keycloak.embedded".equals(args[0])) {
                return "true";
            }

            LOG.info("Invoke on ServletContext: method=[{}] args=[{}]", method.getName(), Arrays.toString(args));

            return method.invoke(servletContext, args);
        };

        return (ServletContext) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }
}
