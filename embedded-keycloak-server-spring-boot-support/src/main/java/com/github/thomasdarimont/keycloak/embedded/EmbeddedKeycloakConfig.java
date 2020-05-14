package com.github.thomasdarimont.keycloak.embedded;

import com.github.thomasdarimont.keycloak.embedded.support.DynamicJndiContextFactoryBuilder;
import com.github.thomasdarimont.keycloak.embedded.support.SpringBootConfigProvider;
import com.github.thomasdarimont.keycloak.embedded.support.SpringBootPlatform;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ParserRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.services.filters.KeycloakSessionServletFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.InputStream;

@Configuration
public class EmbeddedKeycloakConfig {

    @Bean
    @ConditionalOnMissingBean(name = "embeddedKeycloakServer")
    protected EmbeddedKeycloakServer embeddedKeycloakServer(ServerProperties serverProperties) {
        return new EmbeddedKeycloakServer(serverProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "springBootPlatform")
    protected SpringBootPlatform springBootPlatform() {
        return new SpringBootPlatform();
    }

    @Bean
    @ConditionalOnMissingBean(name = "springBootConfigProvider")
    protected SpringBootConfigProvider springBootConfigProvider(KeycloakProperties keycloakProperties) {
        return new SpringBootConfigProvider(keycloakProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "springBeansJndiContextFactory")
    protected DynamicJndiContextFactoryBuilder springBeansJndiContextFactory(DataSource dataSource, DefaultCacheManager cacheManager) {
        return new DynamicJndiContextFactoryBuilder(dataSource, cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean(name = "keycloakInfinispanCacheManager")
    protected DefaultCacheManager keycloakInfinispanCacheManager(KeycloakCustomProperties customProperties) throws Exception {

        KeycloakCustomProperties.Infinispan infinispan = customProperties.getInfinispan();
        try (InputStream inputStream = infinispan.getConfigLocation().getInputStream()) {
            ConfigurationBuilderHolder configBuilder = new ParserRegistry().parse(inputStream);
            DefaultCacheManager defaultCacheManager = new DefaultCacheManager(configBuilder, false);
            defaultCacheManager.start();
            return defaultCacheManager;
        }
    }

    @Bean
    @ConditionalOnMissingBean(name = "keycloakJaxRsApplication")
    protected ServletRegistrationBean<HttpServlet30Dispatcher> keycloakJaxRsApplication(KeycloakCustomProperties customProperties) {

        ServletRegistrationBean<HttpServlet30Dispatcher> servlet = new ServletRegistrationBean<>(new HttpServlet30Dispatcher());
        servlet.addInitParameter("javax.ws.rs.Application", EmbeddedKeycloakApplication.class.getName());

        servlet.addInitParameter("resteasy.allowGzip", "true");
        servlet.addInitParameter("keycloak.embedded", "true");
        servlet.addInitParameter("resteasy.document.expand.entity.references", "false");
        servlet.addInitParameter("resteasy.document.secure.processing.feature", "true");
        servlet.addInitParameter("resteasy.document.secure.disableDTDs", "true");

        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, customProperties.getServer().getKeycloakPath());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "false");
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_DISABLE_HTML_SANITIZER, "true");
        servlet.addUrlMappings(customProperties.getServer().getKeycloakPath() + "/*");
        servlet.setLoadOnStartup(2);
        servlet.setAsyncSupported(true);

        return servlet;
    }

    @Bean
    @ConditionalOnMissingBean(name = "keycloakSessionManagement")
    protected FilterRegistrationBean<KeycloakSessionServletFilter> keycloakSessionManagement(KeycloakCustomProperties customProperties) {

        FilterRegistrationBean<KeycloakSessionServletFilter> filter = new FilterRegistrationBean<>();
        filter.setName("Keycloak Session Management");
        filter.setFilter(new KeycloakSessionServletFilter());
        filter.addUrlPatterns(customProperties.getServer().getKeycloakPath() + "/*");

        return filter;
    }
}
