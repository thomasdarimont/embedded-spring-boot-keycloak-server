package com.github.thomasdarimont.keycloak.embedded;

import lombok.extern.jbosslog.JBossLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@JBossLog
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties(KeycloakProperties.class)
@ServletComponentScan
public class EmbeddedKeycloakServer {

    public static void main(String[] args) {
        SpringApplication.run(EmbeddedKeycloakServer.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(ServerProperties serverProperties, KeycloakProperties keycloakProperties) {

        return (evt) -> {

            Integer port = serverProperties.getPort();
            String keycloakContextPath = keycloakProperties.getServer().getContextPath();

            log.infof("Embedded Keycloak started: Browse to <http://localhost:%d%s> to use keycloak%n", port, keycloakContextPath);
        };
    }
}
