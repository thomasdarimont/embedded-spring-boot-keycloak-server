package com.github.thomasdarimont.keycloak.embedded.support;

import com.google.auto.service.AutoService;
import org.keycloak.platform.Platform;
import org.keycloak.platform.PlatformProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;

public class SpringBootPlatform implements SmartApplicationListener {

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationReadyEvent.class.equals(eventType) || ContextStoppedEvent.class.equals(eventType);
    }

    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ApplicationReadyEvent) {
            runStartupHook();
        } else if (event instanceof ContextStoppedEvent) {
            runShutdownHook();
        }
    }

    protected void runShutdownHook() {
        getPlatformField(Runnable.class, "shutdownHook").ifPresent(Runnable::run);
    }

    protected void runStartupHook() {
        getPlatformField(Runnable.class, "startupHook").ifPresent(Runnable::run);
    }

    // TODO find better way to work around classloader issues
    private <T> Optional<T> getPlatformField(Class<T> type, String name) {
        PlatformProvider p = Platform.getPlatform();
        Field field = ReflectionUtils.findField(p.getClass(), name);
        ReflectionUtils.makeAccessible(field);
        return Optional.ofNullable(type.cast(ReflectionUtils.getField(field, p)));
    }

    @AutoService(PlatformProvider.class)
    public static class Delegate implements PlatformProvider {

        private Runnable startupHook;

        private Runnable shutdownHook;

        @Override
        public void onStartup(Runnable startupHook) {
            this.startupHook = startupHook;
        }

        @Override
        public void onShutdown(Runnable shutdownHook) {
            this.shutdownHook = shutdownHook;
        }

        @Override
        public void exit(Throwable cause) {
            throw new RuntimeException(cause);
        }
    }
}
