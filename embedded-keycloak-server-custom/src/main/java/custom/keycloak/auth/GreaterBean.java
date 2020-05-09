package custom.keycloak.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GreaterBean {

    public void greet(String username) {
        log.info("Hello {}", username);
    }
}
