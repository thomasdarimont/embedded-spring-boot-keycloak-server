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

public class SpringBootPlatform implements SmartApplicationListener {

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationReadyEvent.class.equals(eventType) || ContextStoppedEvent.class.equals(eventType);
    }

    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ApplicationReadyEvent) {
            Runnable startupHook = getPlatformField(Runnable.class, "startupHook");
            if (startupHook != null) {
                startupHook.run();
            }
        } else if (event instanceof ContextStoppedEvent) {
            Runnable shutdownHook = getPlatformField(Runnable.class, "shutdownHook");
            if (shutdownHook != null) {
                shutdownHook.run();
            }
        }
    }

    // TODO find better way to work around classloader issues
    private <T> T getPlatformField(Class<T> type, String name) {
        PlatformProvider p = Platform.getPlatform();
        Field field = ReflectionUtils.findField(p.getClass(), name);
        ReflectionUtils.makeAccessible(field);
        return type.cast(ReflectionUtils.getField(field, p));
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
