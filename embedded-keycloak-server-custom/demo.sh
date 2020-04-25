#!/bin/bash

jgroups_config=${jgroups_config:-dns}
jgroups_configfile=${jgroups_configfile:-jgroups_${jgroups_config:-plain}.xml}
jgroups_configfile_path=${jgroups_configfile_path:-src/main/resources/$jgroups_configfile}

java -version
echo Using JGroups Config: $jgroups_configfile_path

java -Djgroups.configfile=$jgroups_configfile_path \
     -Djgroups.dnsping.dns_query=keycloak-pods.dev.svc.cluster.local \
     -Djava.net.preferIPv4Stack=true \
     -jar target/*.jar