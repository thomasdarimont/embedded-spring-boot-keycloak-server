package de.tdlabs.examples.keycloak;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.services.filters.KeycloakSessionServletFilter;
import org.keycloak.services.listeners.KeycloakSessionDestroyListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EmbeddedKeycloakConfig {

    @Bean
    ServletRegistrationBean keycloakJaxRsApplication(KeycloakServerProperties keycloakServerProperties) {

        //FIXME: hack to propagate Spring Boot Properties to Keycloak Application
        EmbeddedKeycloakApplication.keycloakServerProperties = keycloakServerProperties;

        ServletRegistrationBean servlet = new ServletRegistrationBean(new HttpServlet30Dispatcher());
        servlet.addInitParameter("javax.ws.rs.Application", EmbeddedKeycloakApplication.class.getName());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, keycloakServerProperties.getContextPath());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "true");
        servlet.addUrlMappings(keycloakServerProperties.getContextPath() + "/*");
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);

        return servlet;
    }

    @Bean
    ServletListenerRegistrationBean<KeycloakSessionDestroyListener> keycloakSessionDestroyListener() {
        return new ServletListenerRegistrationBean<>(new KeycloakSessionDestroyListener());
    }

    @Bean
    FilterRegistrationBean keycloakSessionManagement(KeycloakServerProperties keycloakServerProperties) {

        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setName("Keycloak Session Management");
        filter.setFilter(new KeycloakSessionServletFilter());
        filter.addUrlPatterns(keycloakServerProperties.getContextPath() + "/*");

        return filter;
    }
}
