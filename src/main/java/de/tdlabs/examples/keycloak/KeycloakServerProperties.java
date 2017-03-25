package de.tdlabs.examples.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by tom on 12.06.16.
 */
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakServerProperties {

    String contextPath = "/auth";

    String adminUsername = "admin";

    String adminPassword = "admin";

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
