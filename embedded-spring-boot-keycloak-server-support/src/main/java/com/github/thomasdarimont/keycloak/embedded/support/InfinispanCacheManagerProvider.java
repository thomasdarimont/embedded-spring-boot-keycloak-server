package com.github.thomasdarimont.keycloak.embedded.support;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.cluster.ManagedCacheManagerProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;

@AutoService(ManagedCacheManagerProvider.class)
public class InfinispanCacheManagerProvider implements ManagedCacheManagerProvider {

    public static final String JNDI_NAME = "spring/infinispan/cacheManager";

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getCacheManager(Config.Scope config) {
        try {
            return (C) new InitialContext().lookup(JNDI_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
