package de.tdlabs.examples.keycloak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties(KeycloakServerProperties.class)
public class EmbeddedKeycloakApp {

  public static void main(String[] args) {
    SpringApplication.run(EmbeddedKeycloakApp.class, args);
  }

  @Bean
  ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(ServerProperties serverProperties, KeycloakServerProperties keycloakServerProperties) {

    return (evt) -> {

      Integer port = serverProperties.getPort();
      String rootContextPath = serverProperties.getContextPath();
      String keycloakContextPath = keycloakServerProperties.getContextPath();

      System.out.printf("Embedded Keycloak started: http://localhost:%s%s%s to use keycloak%n", port, rootContextPath, keycloakContextPath);
    };
  }
}
