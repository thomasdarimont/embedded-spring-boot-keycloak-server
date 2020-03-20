package com.github.thomasdarimont.keycloak.embedded;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.common.Profile;
import org.keycloak.common.Version;
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

        log.infof("Using Keycloak Version: %s", Version.VERSION_KEYCLOAK);
        log.infof("Enabled Keycloak Features (Deprecated): %s", Profile.getDeprecatedFeatures());
        log.infof("Enabled Keycloak Features (Preview): %s", Profile.getPreviewFeatures());
        log.infof("Enabled Keycloak Features (Experimental): %s", Profile.getExperimentalFeatures());
        log.infof("Enabled Keycloak Features (Disabled): %s", Profile.getDisabledFeatures());

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
