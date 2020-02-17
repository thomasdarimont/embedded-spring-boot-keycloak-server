package com.github.thomasdarimont.keycloak.embedded;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties extends HashMap<String, Object> {

    Server server = new Server();

    AdminUser adminUser = new AdminUser();

    @Getter
    @Setter
    public static class Server {

        String contextPath = "/auth";
    }

    @Getter
    @Setter
    public static class Infinispan {
        String configLocation;
    }

    @Getter
    @Setter
    public static class AdminUser {

        String username = "admin";

        String password = "admin";
    }
}
