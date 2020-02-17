package com.github.thomasdarimont.keycloak.embedded.support;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.common.util.ResteasyProvider;

public class Resteasy3Provider implements ResteasyProvider {

    @Override
    public <R> R getContextData(Class<R> type) {
        return resteasyProviderFactory().getContextData(type);
    }

    @Override
    public void pushDefaultContextObject(Class type, Object instance) {
        Dispatcher contextData = resteasyProviderFactory().getContextData(Dispatcher.class);
        contextData.getDefaultContextObjects().put(type, instance);
    }

    @Override
    public void pushContext(Class type, Object instance) {
        resteasyProviderFactory().pushContext(type, instance);
    }

    @Override
    public void clearContextData() {
        resteasyProviderFactory().clearContextData();
    }


    private ResteasyProviderFactory resteasyProviderFactory() {
        return ResteasyProviderFactory.getInstance();
    }
}
