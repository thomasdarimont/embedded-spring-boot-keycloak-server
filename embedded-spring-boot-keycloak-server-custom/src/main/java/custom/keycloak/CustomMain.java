package custom.keycloak;

import com.github.thomasdarimont.keycloak.embedded.EmbeddedKeycloakServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = EmbeddedKeycloakServer.class)
public class CustomMain {

    public static void main(String[] args) {
        SpringApplication.run(CustomMain.class, args);
    }
}
