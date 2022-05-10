Embedded Keycloak Server running in a Spring Boot App 
=====================================================

This project provides an embedded Authentication and Authorization Server 
based on [Keycloak](https://www.keycloak.org) and [Spring Boot](https://spring.io/projects/spring-boot).  
The idea is to have a variant of [Keycloak-X](https://www.keycloak.org/2019/10/keycloak-x) but based on 
Spring Boot instead of Quarkus.

Keycloak is embedded by hosting it's JAX-RS Application in a Spring-Boot environment.  

## Compatibility  

The following table shows the Keycloak versions used by the embedded Keycloak Server version.   

Embedded Keycloak Server | Keycloak | Spring Boot
---|----------|---
1.x.y | 9.0.3    | 2.2.7.RELEASE
2.3.y | 10.0.2   | 2.3.1.RELEASE
2.4.y | 11.0.2   | 2.3.3.RELEASE
3.0.y | 12.0.4   | 2.4.4
4.0.y | 13.0.1   | 2.4.6
5.0.y | 15.0.2   | 2.5.4
5.1.y | 15.1.1   | 2.5.10
6.0.y | 16.1.1   | 2.5.10
7.0.y | 17.0.1   | 2.6.7
8.0.y | 18.0.0   | 2.6.7

## Modules

### embedded-keycloak-server-spring-boot-support
This module contains the necessary bits to embed a Keycloak server
in a Spring Boot app.

### embedded-keycloak-server-spring-boot-starter
This module contains a Spring Boot starter for an Embedded Keycloak Server. 

### embedded-keycloak-server-plain
This is an example module showing the raw embed a Keycloak server
in a Spring Boot app without additional customizations.

### embedded-keycloak-server-custom
This is an example module showing how to embed a Keycloak server in a Spring Boot app with additional customizations.

## Installation

To add Keycloak to a Spring Boot project, add a dependency to the Spring Boot starter and make sure to use this project's BOM/parent so that you're getting all the right dependency versions:

Note that the artifacts are currently distributed via [jitpack](https://jitpack.io/), see the corresponding [jitpack project](https://jitpack.io/#thomasdarimont/embedded-spring-boot-keycloak-server).

In Maven:
``` xml
<project ...>
  <parent>
      <groupId>com.github.thomasdarimont.embedded-spring-boot-keycloak-server</groupId>
      <artifactId>embedded-keycloak-server-spring-boot-parent</artifactId>
      <version>8.0.0</version>
  </parent>

  <dependencies>
        <dependency>
            <groupId>com.github.thomasdarimont.embedded-spring-boot-keycloak-server</groupId>
            <artifactId>embedded-keycloak-server-spring-boot-starter</artifactId>
            <version>8.0.0</version>
        </dependency>
  </dependencies>

...

    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

  ...
</project>
```

In Gradle:
``` groovy
plugins {
	id 'org.springframework.boot' version '2.6.7'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

repositories {
  mavenCentral()
  maven { url "https://jitpack.io" }
}

dependencyManagement {
  imports {
    mavenBom 'com.github.thomasdarimont.embedded-spring-boot-keycloak-server:embedded-keycloak-server-spring-boot-parent:8.0.0'
  }
}

dependencies {
  implementation "com.github.thomasdarimont.embedded-spring-boot-keycloak-server:embedded-keycloak-server-spring-boot-starter:8.0.0"
}
```

Make sure you chose a version that matches the Keycloak version you want to use from the compatibility table above.

## Build

To build the embedded Spring Boot Keycloak Server, run the following command:
Note: we use the `install` goal to install the artifacts into the local maven repository  
in order to be able to consume the artifacts in our customization project.   
```
mvn clean install
```

## Run
To run the plain embedded keycloak server example app, you can execute the following command:
```
java -jar embedded-keycloak-server-plain/target/*.jar
```

The embedded Keycloak server is now reachable via http://localhost:8080/auth

Note: If you didn't configure an admin password explicitly, we will generate the password at startup and print it to the console.  
You can use this password to login as the user `admin`.
```
2020-07-07 16:02:39.531  INFO 13974 --- [           main] c.g.t.k.e.EmbeddedKeycloakApplication    : Generated admin password: 15909ee9-871d-4caf-ad04-5da5f3e0838f		
```

## Configuration

The Keycloak server part can be configured via Spring Boot configuration mechanism.  
See `embedded-keycloak-server-plain/application.yml` for a list of configurable settings.

## Customizing

The `embedded-keycloak-server-custom` example project demonstrates how one can use the  
`embedded-keycloak-server-spring-boot-starter` library to create an embedded Keycloak server with additional   
customizations like Keycloak extensions and a custom theme.  

## Clustering
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


## Current gotchas

None.