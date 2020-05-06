Example for Embedded Keycloak Server Customizations
---

The projects demonstrates how to use the `embedded-keycloak-server-spring-boot-starter` with
custom extensions, themes and configuration.

# Configuration

## Using other databases

Thanks to the Spring Boot integration, other databases can be easily configured.
As an example we show how to use the PostgreSQL database. 

Add the postgres dependency to your `pom.xml`.
```
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

Adjust the datasource configuration in `application.yml`.
```
  datasource:
    username: keycloak
    password: keycloak
    url: jdbc:postgresql://localhost:5432/embd_keycloak
    hikari:
      maximum-pool-size: 25
      minimum-idle: 1
    driver-class-name: org.postgresql.Driver
```

# Snippets

## JGroups

### Using TCPPING

```
java -Djgroups.configfile=jgroups.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djgroups.tcpping.initial_hosts='gauss[7600],neumann[7600]' \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```

### Using DNS_PING

```
java -Djgroups.configfile=jgroups_dns.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djgroups.dnsping.dns_query=keycloak.thomasdarimont.local \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```

### Using custom jgroups configuration

```
java -Djgroups.configfile=path/to/jgroups.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```
