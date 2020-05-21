Embedded Keycloak Server running in a Spring Boot App 
----------------------------------------------------------

This project provides an embedded Authentication and Authorization Server 
based on [Keycloak](https://www.keycloak.org) and [Spring Boot](https://spring.io/projects/spring-boot).  
The idea is to have a variant of [Keycloak-X](https://www.keycloak.org/2019/10/keycloak-x) but based on 
Spring Boot instead of Quarkus.

Keycloak is embedded by hosting it's JAX-RS Application in a Spring-Boot environment.  

# Compatibility  

The following table shows the Keycloak versions used by the embedded Keycloak Server version.   

Embedded Keycloak Server | Keycloak
---|---
1.x.y | 9.0.3
2.x.y | 10.0.1

# Modules

## embedded-keycloak-server-spring-boot-support
This module contains the necessary bits to embed a Keycloak server
in a Spring Boot app.

## embedded-keycloak-server-spring-boot-starter
This module contains a Spring Boot starter for an Embedded Keycloak Server. 

## embedded-keycloak-server-plain
This module contains the raw embed a Keycloak server
in a Spring Boot app without additional customizations.

## embedded-keycloak-server-custom
This module contains the embed a Keycloak server in a Spring Boot app with additional customizations.

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
java -jar embedded-keycloak-server-plain/target/*.jar
```

The embedded Keycloak server is now reachable via http://localhost:8080/auth

# Configuration

The Keycloak server part can be configured via Spring Boot configuration mechanism.  
See `embedded-keycloak-server-plain/application.yml` for a list of configurable settings.

# Customizing

The `embedded-keycloak-server-custom` example project demonstrates how one can use the  
`embedded-keycloak-server-spring-boot-starter` library to create an embedded Keycloak server with additional   
customizations like Keycloak extensions and a custom theme.  

# Clustering
The embedded Keycloak server uses JGroups for Peer-to-Peer cluster communication and Infinispan for  
managing distributed caches like SSO-Sessions etc.  

JGroups Clustering can be configured via the `jgroups.xml` configuration file.  
Infinispan distributed caches can be configured via the `infinispan.xml` configuration file.  

By default JGroups is configured with `TCPPING` discovery which requires a list of initial hostnames 
to join a cluster. If you want another JGroups discovery mechanism like, e.g. dnsping or kube_ping, 
then you just need to adapt the `jgroups.xml` configuration file. Note that some discovery strategies like
kube_ping need additional jars in the classpath.

> Note, that you need to use a centralized database if you want to really leverage a clustered embedded Keycloak.

To see the clustering in action, just build the project and run the following command on two nodes in the `embedded-keycloak-server-plain` directory:

> Run on Node1:
```
java -Djgroups.configfile=jgroups.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djgroups.tcpping.initial_hosts='node1[7600],node2[7600]' \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```

> Run on Node2:
```
java -Djgroups.configfile=jgroups.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djgroups.tcpping.initial_hosts='node1[7600],node2[7600]' \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```

> Note, the expression `$(hostname -I | cut -d' ' -f1)` takes the first host IP address as the bind adress for JGroups.

If the clustering works you should see messages like:
```
2020-04-19 11:29:16.665  INFO 17055 --- [PN,neumann-3283] org.infinispan.CLUSTER                   : ISPN000094: Received new cluster view for channel ISPN: [neumann-3283|1] (2) [neumann-3283, gauss-45273]
2020-04-19 11:29:16.668  INFO 17055 --- [PN,neumann-3283] org.infinispan.CLUSTER                   : ISPN100000: Node gauss-45273 joined the cluster
2020-04-19 11:29:17.005  INFO 17055 --- [e-thread--p2-t2] org.infinispan.CLUSTER                   : [Context=org.infinispan.CONFIG] ISPN100002: Starting rebalance with members [neumann-3283, gauss-45273], phase READ_OLD_WRITE_ALL, topology id 2
...
```


# Current gotchas

## Infinispan and JGroups compatibility
Currently, the latest infinispan which Keycloak supports is `9.4.19.Final`. 

## Resteasy compatibility
The current Keycloak codebase is only compatible with Resteasy 3.x.
