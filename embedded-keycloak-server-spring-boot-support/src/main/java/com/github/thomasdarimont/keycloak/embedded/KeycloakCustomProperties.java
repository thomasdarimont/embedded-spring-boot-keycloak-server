package com.github.thomasdarimont.keycloak.embedded;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak.custom")
public class KeycloakCustomProperties {

    Server server = new Server();

    AdminUser adminUser = new AdminUser();

    Migration migration = new Migration();

    Infinispan infinispan = new Infinispan();

    @Getter
    @Setter
    public static class Server {

        /**
         * Path relative to {@code server.servlet.context-path} for the Keycloak JAX-RS Application to listen to.
         */
        String keycloakPath = "/auth";
    }

    @Getter
    @Setter
    public static class Migration {

        Resource importLocation = new FileSystemResource("keycloak-realm-config.json");

        String importProvider = "singleFile";
    }

    @Getter
    @Setter
    public static class Infinispan {

        Resource configLocation = new ClassPathResource("infinispan.xml");
    }

    @Getter
    @Setter
    public static class AdminUser {

        boolean createAdminUserEnabled = true;

        String username = "admin";

        String password;
    }
}
