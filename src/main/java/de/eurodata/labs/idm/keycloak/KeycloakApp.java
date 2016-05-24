package de.eurodata.labs.idm.keycloak;

import javax.naming.Context;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.keycloak.services.filters.KeycloakSessionServletFilter;
import org.keycloak.services.listeners.KeycloakSessionDestroyListener;
import org.keycloak.services.resources.KeycloakApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
public class KeycloakApp {

	static {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, DelegatingInitialContextFactory.class.getName());
	}

	public static void main(String[] args) {
		SpringApplication.run(KeycloakApp.class, args);
	}	

	@Bean
	public ServletRegistrationBean keycloakRestInterface() {

		ServletRegistrationBean servlet = new ServletRegistrationBean(new HttpServlet30Dispatcher());
		servlet.addInitParameter("javax.ws.rs.Application", KeycloakApplication.class.getName());
		servlet.addInitParameter("resteasy.servlet.mapping.prefix", "/");
		servlet.addUrlMappings("/*");
		servlet.setLoadOnStartup(1);
		servlet.setAsyncSupported(true);

		return servlet;
	}

	@Bean
	public ServletListenerRegistrationBean<KeycloakSessionDestroyListener> keycloakSessionDestroyListener() {
		return new ServletListenerRegistrationBean<>(new KeycloakSessionDestroyListener());
	}

	@Bean
	public FilterRegistrationBean keycloakSessionManagement() {

		FilterRegistrationBean filter = new FilterRegistrationBean();
		filter.setName("Keycloak Session Management");
		filter.setFilter(new KeycloakSessionServletFilter());
		filter.addUrlPatterns("/*");

		return filter;
	}
}
