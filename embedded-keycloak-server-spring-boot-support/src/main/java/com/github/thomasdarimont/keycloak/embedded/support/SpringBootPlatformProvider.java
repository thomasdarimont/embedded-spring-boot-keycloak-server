package com.github.thomasdarimont.keycloak.embedded.support;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.platform.PlatformProvider;
import org.keycloak.services.ServicesLogger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SmartApplicationListener;

import java.io.File;

@Slf4j
@AutoService(PlatformProvider.class)
public class SpringBootPlatformProvider implements PlatformProvider, SmartApplicationListener {

    protected File tmpDir;

    protected Runnable onStartup;

    protected Runnable onShutdown;

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

    @Override
    public File getTmpDirectory() {
        return tmpDir;
    }

    @Override
    public ClassLoader getScriptEngineClassLoader(Config.Scope scope) {
        throw new UnsupportedOperationException();
    }

    protected void shutdown() {
        this.onShutdown.run();
    }

    protected void startup() {
        this.tmpDir = createTempDir();
        this.onStartup.run();
    }

    protected File createTempDir() {
        String tmpDirBase = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tmpDirBase, "keycloak-spring-tmp");
        boolean couldCreateDirs = tmpDir.mkdirs();
        if (couldCreateDirs || tmpDir.exists()) {
            log.debug("Using server tmp directory: {}", tmpDir.getAbsolutePath());
            return tmpDir;
        }

        throw new RuntimeException("Failed to create temp directory: " + tmpDir.getAbsolutePath());
    }
}