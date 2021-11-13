package com.github.thomasdarimont.keycloak;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.github.thomasdarimont.keycloak.embedded.runner.Main;

@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class EmbeddedSpringBootKeycloakServerXApplicationTests {

    @Test
    void contextLoads() {
    }

}
