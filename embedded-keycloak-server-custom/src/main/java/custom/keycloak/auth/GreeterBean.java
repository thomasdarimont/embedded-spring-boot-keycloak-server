package custom.keycloak.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GreeterBean {

    private static Logger log = LoggerFactory.getLogger(GreeterBean.class);

    public void greet(String username) {
        log.info("Hello {}", username);
    }
}
