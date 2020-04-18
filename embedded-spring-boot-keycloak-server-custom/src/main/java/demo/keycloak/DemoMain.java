package demo.keycloak;

import com.github.thomasdarimont.keycloak.embedded.EmbeddedKeycloakServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = EmbeddedKeycloakServer.class)
public class DemoMain {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoMain.class, args);
    }
}
