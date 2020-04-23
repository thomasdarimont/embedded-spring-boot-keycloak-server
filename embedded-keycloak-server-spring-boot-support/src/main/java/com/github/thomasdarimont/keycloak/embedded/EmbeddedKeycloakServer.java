package com.github.thomasdarimont.keycloak.embedded;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.common.Profile;
import org.keycloak.common.Version;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@JBossLog
@RequiredArgsConstructor
public class EmbeddedKeycloakServer {

    private final ServerProperties serverProperties;

    @Bean
    public ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener() {

        return (evt) -> {

            log.infof("Using Keycloak Version: %s", Version.VERSION_KEYCLOAK);
            log.infof("Enabled Keycloak Features (Deprecated): %s", Profile.getDeprecatedFeatures());
            log.infof("Enabled Keycloak Features (Preview): %s", Profile.getPreviewFeatures());
            log.infof("Enabled Keycloak Features (Experimental): %s", Profile.getExperimentalFeatures());
            log.infof("Enabled Keycloak Features (Disabled): %s", Profile.getDisabledFeatures());

            Integer port = serverProperties.getPort();

            log.infof("Embedded Keycloak started: Browse to <http://localhost:%d%s> to use keycloak%n", port, serverProperties.getServlet().getContextPath());
        };
    }
}
