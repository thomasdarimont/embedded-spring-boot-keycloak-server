Simple example for embedding Keycloak in a Spring Boot App 
----------------------------------------------------------
This is just a Proof of Concept and not recommended for production use.
Nevertheless it is a great way develop and test Keycloak extensions or 
generally playing with Keycloak. 


To start the sample application just build the project with: 
```
mvn package
```

and start the spring boot app via:
```
java -jar target/spring-boot-keycloak-server-example-*.BUILD-SNAPSHOT.jar
```

The embedded Keycloak server is now reachable via http://localhost:8080/auth.




