package com.github.thomasdarimont.keycloak.embedded.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * Loads the default keycloak configuration into the environment.
 */
@Slf4j
public class PopulateKeycloakPropertiesApplicationListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {

        if (!(event instanceof ApplicationEnvironmentPreparedEvent)) {
            return;
        }

        ApplicationEnvironmentPreparedEvent envEvent = (ApplicationEnvironmentPreparedEvent) event;
        ConfigurableEnvironment env = envEvent.getEnvironment();

        try {
            Resource resource = new ClassPathResource("keycloak-defaults.yml");

            if (!resource.exists()) {
                return;
            }

            log.info("Loading default keycloak properties configuration from: {}", resource.getURI());

            YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
            List<PropertySource<?>> yamlTestProperties = sourceLoader.load("keycloak-defaults.yml", resource);
            if (!yamlTestProperties.isEmpty()) {
                env.getPropertySources().addLast(yamlTestProperties.get(0));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
