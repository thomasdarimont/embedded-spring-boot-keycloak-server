package com.github.thomasdarimont.keycloak.embedded.support;

import com.github.thomasdarimont.keycloak.embedded.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.keycloak.Config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Delegates Config Key lookups to Spring Boot Properties.
 */
public class SpringBootConfigProvider implements Config.ConfigProvider {

    private static SpringBootConfigProvider INSTANCE;

    private final KeycloakProperties keycloakProperties;

    public SpringBootConfigProvider(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
        INSTANCE = this;
    }

    public static SpringBootConfigProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public String getProvider(String spi) {
        return scope(spi).get("provider");
    }

    @Override
    public Config.Scope scope(String... scopes) {

        if (scopes == null || scopes.length == 0) {
            return new MapScope(keycloakProperties);
        }

        String scope = scopes[0];
        Object currentScopeValue = keycloakProperties.get(scope);
        if (!(currentScopeValue instanceof Map)) {
            return new MapScope(Collections.emptyMap());
        }

        Map<String, Object> scopeMap = (Map<String, Object>) currentScopeValue;
        for (int i = 1; i < scopes.length; i++) {
            currentScopeValue = scopeMap.get(scopes[i]);
            if (currentScopeValue instanceof Map) {
                scopeMap = (Map<String, Object>) currentScopeValue;
            }
        }

        return new MapScope(scopeMap);
    }

    @RequiredArgsConstructor
    class MapScope implements Config.Scope {

        private final Map<String, Object> map;

        private Object getObject(String key, Object defaultValue) {
            Object obj = map.get(key);
            if (obj == null) {
                return defaultValue;
            }
            return obj;
        }

        @Override
        public String get(String key) {
            return get(key, null);
        }

        @Override
        public String get(String key, String defaultValue) {
            Object obj = getObject(key, defaultValue);
            if (obj == null) {
                return defaultValue;
            }
            return String.valueOf(obj);
        }

        @Override
        public String[] getArray(String key) {
            Object obj = getObject(key, null);
            if (obj == null) {
                return null;
            }

            // TODO find better way to parse config yaml into a list instead of a LinkedHashMap in the first place
            if (obj instanceof LinkedHashMap) {
                LinkedHashMap lm = (LinkedHashMap) obj;
                String[] strings = new String[lm.size()];
                int i = 0;
                for (Object entryValue : lm.values()) {
                    strings[i++] = String.valueOf(entryValue);
                }
                return strings;
            }

            return (String[]) obj;
        }

        @Override
        public Integer getInt(String key) {
            return getInt(key, null);
        }

        @Override
        public Integer getInt(String key, Integer defaultValue) {
            Object obj = getObject(key, null);
            if (obj == null) {
                return defaultValue;
            }
            return Integer.valueOf(String.valueOf(obj));
        }

        @Override
        public Long getLong(String key) {
            return getLong(key, null);
        }

        @Override
        public Long getLong(String key, Long defaultValue) {
            Object obj = getObject(key, null);
            if (obj == null) {
                return defaultValue;
            }
            return Long.valueOf(String.valueOf(obj));
        }

        @Override
        public Boolean getBoolean(String key) {
            return getBoolean(key, null);
        }

        @Override
        public Boolean getBoolean(String key, Boolean defaultValue) {
            Object obj = getObject(key, null);
            if (obj == null) {
                return defaultValue;
            }
            return Boolean.valueOf(String.valueOf(obj));
        }

        @Override
        public Config.Scope scope(String... path) {
            return SpringBootConfigProvider.this.scope(path);
        }

        @Override
        public Set<String> getPropertyNames() {
            return map.keySet();
        }
    }
}
