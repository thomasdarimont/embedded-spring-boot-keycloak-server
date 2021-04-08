package com.github.thomasdarimont.keycloak.embedded.support;

import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Optional;

public class KeycloakInitialContext extends InitialContext {

    private final Hashtable<?, ?> environment;

    public KeycloakInitialContext(Hashtable<?, ?> environment) throws NamingException {
        super(environment);
        this.environment = environment;
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString());
    }

    @Override
    public Object lookup(String name) throws NamingException {
        return Optional.ofNullable(environment.get(name))
                .orElseThrow(() -> new NamingException("Name " + name + " not found"));
    }

    @Override
    public NameParser getNameParser(String name) {
        return CompositeName::new;
    }
}
