package com.github.thomasdarimont.keycloak.embedded;

import com.github.thomasdarimont.keycloak.embedded.support.InfinispanCacheManagerProvider;
import com.github.thomasdarimont.keycloak.embedded.support.SpringBootConfigProvider;
import com.github.thomasdarimont.keycloak.embedded.support.SpringBootPlatform;
import lombok.RequiredArgsConstructor;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ParserRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.services.filters.KeycloakSessionServletFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
class EmbeddedKeycloakConfig {

    private final KeycloakProperties keycloakProperties;

    private final KeycloakCustomProperties customProperties;

    @Bean
    @Lazy
    SpringBootPlatform springBootPlatform() {
        return new SpringBootPlatform();
    }

    @Bean
    SpringBootConfigProvider springBootConfigProvider(KeycloakProperties keycloakProperties) {
        return new SpringBootConfigProvider(keycloakProperties);
    }

    @Bean
    DefaultCacheManager keycloakInfinispanCacheManager() throws Exception {

        KeycloakCustomProperties.Infinispan infinispan = customProperties.getInfinispan();
        try (InputStream inputStream = infinispan.getConfigLocation().getInputStream()) {
            ConfigurationBuilderHolder builder = new ParserRegistry().parse(inputStream);
            return new DefaultCacheManager(builder, true);
        }
    }

    @Autowired
    void mockJndiEnvironment(DataSource dataSource, DefaultCacheManager infinispanCacheManager) throws NamingException {

        NamingManager.setInitialContextFactoryBuilder((env) -> (environment) -> new InitialContext() {

            @Override
            public Object lookup(Name name) {
                return lookup(name.toString());
            }

            @Override
            public Object lookup(String name) {

                if ("spring/datasource".equals(name)) {
                    return dataSource;
                }

                if (InfinispanCacheManagerProvider.JNDI_NAME.equals(name)) {
                    return infinispanCacheManager;
                }

                return null;
            }

            @Override
            public NameParser getNameParser(String name) {
                return CompositeName::new;
            }

            @Override
            public void close() {
                //NOOP
            }
        });
    }

    @Bean
    ServletRegistrationBean<HttpServlet30Dispatcher> keycloakJaxRsApplication(SpringBootConfigProvider configProvider) {

        //FIXME: hack to propagate Spring Boot Properties to Keycloak Application
        EmbeddedKeycloakApplication.keycloakProperties = keycloakProperties;

        //FIXME: hack to propagate Spring Boot Properties to Keycloak Application
        EmbeddedKeycloakApplication.customProperties = customProperties;

        //FIXME: hack to propagate Spring Boot ConfigProvider to Keycloak Application
        EmbeddedKeycloakApplication.configProvider = configProvider;

        ServletRegistrationBean<HttpServlet30Dispatcher> servlet = new ServletRegistrationBean<>(new HttpServlet30Dispatcher());
        servlet.addInitParameter("javax.ws.rs.Application", EmbeddedKeycloakApplication.class.getName());
        String keycloakContextPath = customProperties.getServer().getContextPath();
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, keycloakContextPath);
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "false");
        servlet.addUrlMappings(keycloakContextPath + "/*");
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);

        return servlet;
    }

    @Bean
    FilterRegistrationBean<KeycloakSessionServletFilter> keycloakSessionManagement() {

        FilterRegistrationBean<KeycloakSessionServletFilter> filter = new FilterRegistrationBean<>();
        filter.setName("Keycloak Session Management");
        filter.setFilter(new KeycloakSessionServletFilter());
        filter.addUrlPatterns(customProperties.getServer().getContextPath() + "/*");

        return filter;
    }
}
