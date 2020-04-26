Example for Customizing Embedded Keycloak Server
---

The projects demonstrates how to use the `embedded-keycloak-server-spring-boot-starter` with
custom extensions, themes and configuration.

# Snippets

## Using TCPPING

```
java -Djgroups.configfile=jgroups.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djgroups.tcpping.initial_hosts='gauss[7600],neumann[7600]' \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```

## Using DNS_PING

```
java -Djgroups.configfile=jgroups_dns.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djgroups.dnsping.dns_query=keycloak.thomasdarimont.local \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```

## Using custom jgroups configuration

```
java -Djgroups.configfile=path/to/jgroups.xml \
     -Djgroups.bind_addr=$(hostname -I | cut -d' ' -f1) \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar
```