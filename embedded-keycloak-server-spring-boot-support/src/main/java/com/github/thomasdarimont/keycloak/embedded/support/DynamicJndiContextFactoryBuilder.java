package com.github.thomasdarimont.keycloak.embedded.support;

import lombok.extern.slf4j.Slf4j;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.beans.BeanUtils;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;

@Slf4j
public class DynamicJndiContextFactoryBuilder implements InitialContextFactoryBuilder {

    public static final String JNDI_SPRING_DATASOURCE = "spring/datasource";

    public static final String JNDI_CACHE_MANAGAER = "spring/infinispan/cacheManager";

    public static final String JNDI_EXECUTOR_SERVICE = "java:jboss/ee/concurrency/executor/storage-provider-threads";

    private final InitialContext fixedInitialContext;

    public DynamicJndiContextFactoryBuilder(DataSource dataSource, DefaultCacheManager cacheManager, ExecutorService executorService) {
        fixedInitialContext = createFixedInitialContext(dataSource, cacheManager, executorService);
    }

    protected InitialContext createFixedInitialContext(DataSource dataSource, DefaultCacheManager cacheManager, ExecutorService executorService) {

        Hashtable<Object, Object> jndiEnv = new Hashtable<>();
        jndiEnv.put(JNDI_SPRING_DATASOURCE, dataSource);
        jndiEnv.put(JNDI_CACHE_MANAGAER, cacheManager);
        jndiEnv.put(JNDI_EXECUTOR_SERVICE, executorService);

        try {
            return new KeycloakInitialContext(jndiEnv);
        } catch (NamingException ne) {
            throw new RuntimeException("Could not create KeycloakInitialContext", ne);
        }
    }

    @PostConstruct
    public void init() {
        try {
            NamingManager.setInitialContextFactoryBuilder(this);
        } catch (NamingException e) {
            log.error("Could not configure InitialContextFactoryBuilder", e);
        }
    }


    /**
     * Create a new {@link InitialContextFactory} based on the given {@code environment}.
     * <p>
     * If the lookup environment is empty, we return a JndiContextFactory that returns a InitialContext which supports a lookups against a fixed set of Spring beans.
     * If the environment is not empty, we try to use the provided java.naming.factory.initial classname to create a JndiContextFactory and
     * delegate further lookups to this instance. Otherwise we simply return {@literal null}.
     *
     * @param environment
     * @return
     */
    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) {

        if (environment == null || environment.isEmpty()) {
            return env -> fixedInitialContext;
        }

        String factoryClassName = (String) environment.get("java.naming.factory.initial");
        if (factoryClassName != null) {
            try {
                // factoryClassName -> com.sun.jndi.ldap.LdapCtxFactory
                Class<?> factoryClass = Thread.currentThread().getContextClassLoader().loadClass(factoryClassName);
                return BeanUtils.instantiateClass(factoryClass, InitialContextFactory.class);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}