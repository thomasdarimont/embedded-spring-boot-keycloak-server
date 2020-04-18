package com.github.thomasdarimont.keycloak.embedded;

import org.springframework.boot.SpringApplication;

public class Main {

    public static void main(String[] args) {
        SpringApplication.run(EmbeddedKeycloakServer.class, args);
    }

}
