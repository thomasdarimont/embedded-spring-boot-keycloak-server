Embedded Keycloak Server running in a Spring Boot App 
----------------------------------------------------------

This project provides an embedded Authentication and Authorization Server
based on [Keycloak](https://www.keycloak.org) and [Spring Boot](https://spring.io/projects/spring-boot).

Keycloak is embedded by hosting it's JAX-RS Application in a Spring-Boot environment. 
 

# Build

To build the embedded Spring Boot Keycloak Server, run the following command:
Note: we use the `install` goal to install the artifacts into the local maven repository  
in order to be able to consume the artifacts in our customization project.   
```
mvn clean install
```

# Run
To run the plain embedded keycloak server app, you can execute the following command:
```
java -jar embedded-spring-boot-keycloak-server-plain/target/*.jar
```

The embedded Keycloak server is now reachable via http://localhost:8080/auth

# Configuration

The Keycloak server part can be configured via Spring Boot configuration mechanism.
See `embedded-spring-boot-keycloak-server-plain/application.yml` for a list of configurable settings.
