package com.github.thomasdarimont.keycloak.embedded;

import com.github.thomasdarimont.keycloak.embedded.KeycloakProperties.AdminUser;
import org.keycloak.Config;
import org.keycloak.common.util.Resteasy;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

    static KeycloakProperties keycloakProperties;
    static Config.ConfigProvider configProvider;

    public EmbeddedKeycloakApplication(@Context ServletContext context) {

        Resteasy.pushContext(ServletContext.class, augmentToRedirectContextPath(context));
        tryCreateMasterRealmAdminUser();
    }

    protected void loadConfig() {
        Config.init(configProvider);
    }

    private void tryCreateMasterRealmAdminUser() {

        KeycloakSession session = getSessionFactory().create();

        ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);

        AdminUser admin = keycloakProperties.getAdminUser();

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


    private static ServletContext augmentToRedirectContextPath(ServletContext servletContext) {

        ClassLoader classLoader = servletContext.getClassLoader();
        Class<?>[] interfaces = {ServletContext.class};

        InvocationHandler invocationHandler = (proxy, method, args) -> {

            if ("getContextPath".equals(method.getName())) {
                return keycloakProperties.getServer().getContextPath();
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