package com.github.thomasdarimont.keycloak.embedded;

import com.github.thomasdarimont.keycloak.embedded.KeycloakCustomProperties.AdminUser;
import org.keycloak.Config;
import org.keycloak.common.util.Resteasy;
import org.keycloak.exportimport.ExportImportConfig;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakTransactionManager;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;

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

        if (!customProperties.getAdminUser().isCreateAdminUserEnabled()) {
            LOG.warn("Skipping creation of keycloak master adminUser.");
            return;
        }

        AdminUser adminUser = customProperties.getAdminUser();

        if (StringUtils.isEmpty(adminUser.getUsername()) || StringUtils.isEmpty(adminUser.getPassword())) {
            return;
        }

        KeycloakSession session = getSessionFactory().create();
        KeycloakTransactionManager transaction = session.getTransactionManager();
        try {
            transaction.begin();

            new ApplianceBootstrap(session).createMasterRealmUser(adminUser.getUsername(), adminUser.getPassword());
            ServicesLogger.LOGGER.addUserSuccess(adminUser.getUsername(), Config.getAdminRealm());

            transaction.commit();
        } catch (IllegalStateException e) {
            transaction.rollback();
            ServicesLogger.LOGGER.addUserFailedUserExists(adminUser.getUsername(), Config.getAdminRealm());
        } catch (Throwable t) {
            transaction.rollback();
            ServicesLogger.LOGGER.addUserFailed(t, adminUser.getUsername(), Config.getAdminRealm());
        } finally {
            session.close();
        }
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

        var keycloakServletContextInitParameters = new HashMap<>();
        keycloakServletContextInitParameters.put("resteasy.allowGzip", "true");
        keycloakServletContextInitParameters.put("keycloak.embedded", "true");
        keycloakServletContextInitParameters.put("resteasy.document.expand.entity.references", "false");
        keycloakServletContextInitParameters.put("resteasy.document.secure.processing.feature", "true");
        keycloakServletContextInitParameters.put("resteasy.document.secure.disableDTDs", "true");

        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                if ("getContextPath".equals(method.getName())) {
                    return server.getContextPath();
                }

                if ("getInitParameter".equals(method.getName()) && args.length == 1 && keycloakServletContextInitParameters.containsKey(args[0])) {
                    return keycloakServletContextInitParameters.get(args[0]);
                }

                LOG.info("Invoke on ServletContext: method=[{}] args=[{}]", method.getName(), Arrays.toString(args));

                return method.invoke(servletContext, args);
            }
        };

        return (ServletContext) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }
}
