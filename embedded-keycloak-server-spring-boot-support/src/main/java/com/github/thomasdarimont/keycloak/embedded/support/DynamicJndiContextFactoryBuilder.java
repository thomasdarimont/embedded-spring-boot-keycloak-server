package com.github.thomasdarimont.keycloak.embedded.support;

import lombok.extern.slf4j.Slf4j;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.beans.BeanUtils;

import javax.annotation.PostConstruct;
import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import java.util.Hashtable;

@Slf4j
public class DynamicJndiContextFactoryBuilder implements InitialContextFactoryBuilder {

    public static final String JNDI_SPRING_DATASOURCE = "spring/datasource";

    public static final String JNDI_CACHE_MANAGAER = "spring/infinispan/cacheManager";

    private final InitialContext fixedInitialContext;

    public DynamicJndiContextFactoryBuilder(DataSource dataSource, DefaultCacheManager cacheManager) {
        fixedInitialContext = createFixedInitialContext(dataSource, cacheManager);
    }

    protected InitialContext createFixedInitialContext(DataSource dataSource, DefaultCacheManager cacheManager) {

        try {
            return new InitialContext() {

                @Override
                public Object lookup(Name name) {
                    return lookup(name.toString());
                }

                @Override
                public Object lookup(String name) {

                    if (JNDI_SPRING_DATASOURCE.equals(name)) {
                        return dataSource;
                    }

                    if (JNDI_CACHE_MANAGAER.equals(name)) {
                        return cacheManager;
                    }

                    log.warn("JNDI name not found: {}", name);

                    return null;
                }

                @Override
                public NameParser getNameParser(String name) {
                    return CompositeName::new;
                }

                @Override
                public void close() {
                    //NOOP
                }
            };
        } catch (NamingException e) {
            throw new RuntimeException(e);
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