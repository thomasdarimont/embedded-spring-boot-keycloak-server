Example for embedding Keycloak Server in a Spring-Boot App 
----------------------------------------------------------
This is just a Proof of Concept and not recommended for production use,
nevertheless it is a useful foundation to develop and test Keycloak extensions or 
playing with Keycloak in general. 

To start the sample application, just build the project with: 
```
mvn package
```

and start the spring boot app via:
```
java -jar target/*.jar
```

The embedded Keycloak server is now reachable via http://localhost:8080/auth.




