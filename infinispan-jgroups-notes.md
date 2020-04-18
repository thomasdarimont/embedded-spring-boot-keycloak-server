Notes about Infinispan & JGroups
----

# Get rid of JGroups buffer size warnings on linux
On linux you might see messages like:
```
... WARNING: JGRP000015: the send buffer of socket MulticastSocket was set to 1MB, but the OS only allocated 212.99KB. This might lead to performance problems. Please set your max send buffer in the OS correctly (e.g. net.core.wmem_max on Linux)
... WARNING: JGRP000015: the receive buffer of socket MulticastSocket was set to 20MB, but the OS only allocated 212.99KB. This might lead to performance problems. Please set your max receive buffer in the OS correctly (e.g. net.core.rmem_max on Linux)
```

The following settings need to be adjusted to make the problem go away:

```
# Defaults
# net.core.rmem_default=262144
# net.core.wmem_default=262144

# Adjust
sysctl -w net.core.rmem_max=25600000
sysctl -w net.core.wmem_max=1024000
```

To permanently apply this settings create a config file in the `/etc/sysctl.d/` dropin folder:
```
$ cat /etc/sysctl.d/47-jgroups.conf     
# Adjusted the following settings to get rid of JGroups warnings
# WARNING: JGRP000015: the send buffer of socket MulticastSocket was set to 1MB, but the OS only allocated 212.99KB. This might lead to performance problems. Please set your max send buffer in the OS correctly (e.g. net.core.wmem_max on Linux)
# WARNING: JGRP000015: the receive buffer of socket MulticastSocket was set to 20MB, but the OS only allocated 212.99KB. This might lead to performance problems. Please set your max receive buffer in the OS correctly (e.g. net.core.rmem_max on Linux)
net.core.rmem_max=25600000
net.core.wmem_max=1024000
```