package com.github.thomasdarimont.keycloak.embedded;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
    public class Migration {

        Resource importLocation;

        String importProvider;
    }

    @Getter
    @Setter
    public class Server {

        String contextPath;
    }

    @Getter
    @Setter
    public class Infinispan {

        Resource configLocation;
    }

    @Getter
    @Setter
    public class AdminUser {

        String username;

        String password;
    }
}
