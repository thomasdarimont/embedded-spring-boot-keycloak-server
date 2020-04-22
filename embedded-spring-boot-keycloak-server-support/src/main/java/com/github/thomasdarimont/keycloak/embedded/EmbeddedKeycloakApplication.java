package com.github.thomasdarimont.keycloak.embedded;

import com.github.thomasdarimont.keycloak.embedded.KeycloakCustomProperties.AdminUser;
import org.keycloak.Config;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.IOException;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

    private final KeycloakCustomProperties customProperties;

    static Config.ConfigProvider configProvider;

    public EmbeddedKeycloakApplication(@Context ServletContext context) {

        WebApplicationContext webAppContext = WebApplicationContextUtils.getWebApplicationContext(context);
        this.customProperties = webAppContext.getBean(KeycloakCustomProperties.class);
    }

    @Override
    protected ExportImportManager migrateAndBootstrap() {

        ExportImportManager exportImportManager = super.migrateAndBootstrap();

        tryCreateMasterRealmAdminUser();
        tryImportRealm();

        return exportImportManager;
    }

    protected void loadConfig() {
        Config.init(configProvider);
    }

    protected void tryCreateMasterRealmAdminUser() {

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

    protected void tryImportRealm() {

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
}
