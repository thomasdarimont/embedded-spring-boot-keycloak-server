Embedded Keycloak Server running in a Spring Boot App 
----------------------------------------------------------

This project provides an embedded Authentication and Authorization Server
based on [Keycloak](https://www.keycloak.org) and [Spring Boot](https://spring.io/projects/spring-boot).

Keycloak is embedded by hosting it's JAX-RS Application in a Spring-Boot environment. 
 

# Build

To start the embedded Keycloak Server, just build the project with: 
```
mvn package
```

# Run
To run the embedded keycloak server app, you can execute the following command:
```
java -jar target/*.jar
```

The embedded Keycloak server is now reachable via http://localhost:8080/auth

# Configuration

The Keycloak server part can be configured via Spring Boot configuration mechanism.
See `application.yml` for a list of configurable settings.
