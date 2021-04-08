package com.github.thomasdarimont.keycloak.embedded.support;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.platform.PlatformProvider;
import org.keycloak.services.ServicesLogger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SmartApplicationListener;

@Slf4j
@AutoService(PlatformProvider.class)
public class SpringBootPlatformProvider implements PlatformProvider, SmartApplicationListener {

    Runnable onStartup;

    Runnable onShutdown;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ApplicationReadyEvent) {
            startup();
        } else if (event instanceof ContextStoppedEvent) {
            shutdown();
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationReadyEvent.class.equals(eventType) || ContextStoppedEvent.class.equals(eventType);
    }

    @Override
    public String getListenerId() {
        return this.getClass().getName();
    }

    @Override
    public void onStartup(@SuppressWarnings("hiding") Runnable onStartup) {
        this.onStartup = onStartup;
    }

    @Override
    public void onShutdown(@SuppressWarnings("hiding") Runnable onShutdown) {
        this.onShutdown = onShutdown;
    }

    @Override
    public void exit(Throwable cause) {
        log.error("exit", cause);
        ServicesLogger.LOGGER.fatal(cause);
        throw new RuntimeException(cause);
    }

    private void shutdown() {
        this.onShutdown.run();
    }

    private void startup() {
        this.onStartup.run();
    }
}