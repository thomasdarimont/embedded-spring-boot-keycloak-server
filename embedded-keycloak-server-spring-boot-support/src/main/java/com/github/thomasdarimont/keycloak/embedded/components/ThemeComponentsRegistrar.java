package com.github.thomasdarimont.keycloak.embedded.components;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.KeycloakDeploymentInfo;
import org.keycloak.provider.ProviderManager;
import org.keycloak.provider.ProviderManagerDeployer;
import org.keycloak.provider.ProviderManagerRegistry;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * {@link ThemeComponentsRegistrar} discovers script based components like Authenticators, OIDC Mappers and Authorization Policies.
 */
@Slf4j
@AutoService(AuthenticatorFactory.class)
public class ThemeComponentsRegistrar extends AbstractComponentsRegistrar {

    public static final String THEME_RESOURCES_LOCATION = "theme-resources";

    @Override
    public String getId() {
        return "theme-component-registrar";
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

        // TODO differentiate between theme-resources and themes
        List<URL> themes = discoverEmbeddedThemes();
        if (!CollectionUtils.isEmpty(themes)) {
            registerEmbeddedThemes((ProviderManagerDeployer) factory, themes);
        }
    }

    private List<URL> discoverEmbeddedThemes() {

        try {
            List<URL> themeResources = Collections.list(Thread.currentThread().getContextClassLoader().getResources(THEME_RESOURCES_LOCATION));
            if (CollectionUtils.isEmpty(themeResources)) {
                log.info("Could not detect any embedded theme components.");
                return Collections.emptyList();
            }

            log.info("Detected embedded theme components. themes={}", themeResources.size());

            return themeResources;
        } catch (IOException e) {
            log.info("Failed to load detect any theme-resources on classpath from {}.", THEME_RESOURCES_LOCATION, e);
        }
        return Collections.emptyList();
    }

    private void registerEmbeddedThemes(ProviderManagerDeployer factory, List<URL> themes) {

        KeycloakDeploymentInfo kdi = KeycloakDeploymentInfo.create().name("theme-components");
        kdi.themeResources();
        kdi.themes();

        ProviderManager pm = new ProviderManager(kdi, Thread.currentThread().getContextClassLoader());

        // Ugly hack to trigger theme deployment...
        Executors.newScheduledThreadPool(1).schedule(() -> {
            log.info("Register theme components");
            ProviderManagerRegistry.SINGLETON.deploy(pm);
        }, 1, TimeUnit.SECONDS);

    }

}
