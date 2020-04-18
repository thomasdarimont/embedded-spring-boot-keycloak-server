package com.github.thomasdarimont.keycloak.embedded.runner;

import com.github.thomasdarimont.keycloak.embedded.EmbeddedKeycloakServer;
import org.springframework.boot.SpringApplication;

public class Main {

    public static void main(String[] args) {
        SpringApplication.run(EmbeddedKeycloakServer.class, args);
    }

}
