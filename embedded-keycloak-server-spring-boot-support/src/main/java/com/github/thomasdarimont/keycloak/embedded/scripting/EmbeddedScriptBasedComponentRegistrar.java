package com.github.thomasdarimont.keycloak.embedded.scripting;

import com.google.auto.service.AutoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.AuthenticatorSpi;
import org.keycloak.authentication.authenticators.browser.DeployedScriptAuthenticatorFactory;
import org.keycloak.authorization.policy.provider.PolicySpi;
import org.keycloak.authorization.policy.provider.js.DeployedScriptPolicyFactory;
import org.keycloak.common.Profile;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.protocol.ProtocolMapperSpi;
import org.keycloak.protocol.oidc.mappers.DeployedScriptOIDCProtocolMapper;
import org.keycloak.provider.EnvironmentDependentProviderFactory;
import org.keycloak.provider.KeycloakDeploymentInfo;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderManager;
import org.keycloak.provider.ProviderManagerDeployer;
import org.keycloak.provider.ProviderManagerRegistry;
import org.keycloak.representations.provider.ScriptProviderDescriptor;
import org.keycloak.representations.provider.ScriptProviderMetadata;
import org.keycloak.util.JsonSerialization;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link EmbeddedScriptBasedComponentRegistrar} discovers script based components like Authenticators, OIDC Mappers and Authorization Policies.
 */
@Slf4j
@AutoService(AuthenticatorFactory.class)
public class EmbeddedScriptBasedComponentRegistrar implements AuthenticatorFactory, EnvironmentDependentProviderFactory {

    public static final String KEYCLOAK_SCRIPTS_JSON_LOCATION = "META-INF/keycloak-scripts.json";

    @Override
    public String getId() {
        return "script-component-registrar";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        // NOT USED
        return null;
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

        KeycloakScripts keycloakScripts = discoverScriptComponents();
        if (keycloakScripts == null) {
            return;
        }

        deployKeycloakScriptComponents((ProviderManagerDeployer) factory, keycloakScripts);
    }

    private KeycloakScripts discoverScriptComponents() {

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(KEYCLOAK_SCRIPTS_JSON_LOCATION)) {

            if (in == null) {
                log.info("Could detect any script-based components.");
                return null;
            }

            KeycloakScripts keycloakScripts = new KeycloakScripts(JsonSerialization.readValue(in, ScriptProviderDescriptor.class));
            List<ScriptProviderMetadata> authenticators = keycloakScripts.getAuthenticators();
            List<ScriptProviderMetadata> oidcMappers = keycloakScripts.getOidcMappers();
            List<ScriptProviderMetadata> policies = keycloakScripts.getPolicies();
            log.info("Detected script-based components. authenticators={} mappers={} policies={}", authenticators.size(), oidcMappers.size(), policies.size());

            return keycloakScripts;
        } catch (IOException e) {
            log.info("Failed to load detect any script-based components on classpath from {}.", KEYCLOAK_SCRIPTS_JSON_LOCATION, e);
        }

        return null;
    }


    private void deployKeycloakScriptComponents(ProviderManagerDeployer factory, KeycloakScripts keycloakScripts) {

        KeycloakDeploymentInfo kdi = KeycloakDeploymentInfo.create().name("script-components");

        List<ScriptProviderMetadata> authenticators = keycloakScripts.getAuthenticators();
        authenticators.stream()
                .map(this::initScriptComponent)
                .forEach(sc -> kdi.addProvider(AuthenticatorSpi.class, new DeployedScriptAuthenticatorFactory(sc)));

        List<ScriptProviderMetadata> oidcMappers = keycloakScripts.getOidcMappers();
        oidcMappers.stream()
                .map(this::initScriptComponent)
                .forEach(sc -> kdi.addProvider(ProtocolMapperSpi.class, new DeployedScriptOIDCProtocolMapper(sc)));

        List<ScriptProviderMetadata> policies = keycloakScripts.getPolicies();
        policies.stream()
                .map(this::initScriptComponent)
                .forEach(sc -> kdi.addProvider(PolicySpi.class, new DeployedScriptPolicyFactory(sc)));

        ProviderManager pm = new ProviderManager(kdi, Thread.currentThread().getContextClassLoader());

        // Ugly hack to trigger script deployment...
        ProviderManagerRegistry.SINGLETON.setDeployer(factory);
        ProviderManagerRegistry.SINGLETON.deploy(pm);
    }

    private ScriptProviderMetadata initScriptComponent(ScriptProviderMetadata scriptMetadata) {

        scriptMetadata.setId("script" + "-" + scriptMetadata.getFileName());

        String name = scriptMetadata.getName();
        if (name == null) {
            name = scriptMetadata.getFileName();
        }
        scriptMetadata.setName(name);

        String scriptLocation = "./scripts/" + scriptMetadata.getFileName();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(scriptLocation)) {
            scriptMetadata.setCode(StreamUtils.copyToString(in, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            log.warn("Cannot load script from {}", scriptMetadata.getFileName());
        }

        return scriptMetadata;
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public boolean isSupported() {
        return Profile.isFeatureEnabled(Profile.Feature.SCRIPTS);
    }

    @Override
    public String getDisplayType() {
        return null;
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[0];
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @RequiredArgsConstructor
    static class KeycloakScripts {

        private final ScriptProviderDescriptor descriptor;

        public List<ScriptProviderMetadata> getAuthenticators() {
            return Optional.ofNullable(descriptor.getProviders().get(ScriptProviderDescriptor.AUTHENTICATORS)).orElse(Collections.emptyList());
        }

        public List<ScriptProviderMetadata> getOidcMappers() {
            return Optional.ofNullable(descriptor.getProviders().get(ScriptProviderDescriptor.MAPPERS)).orElse(Collections.emptyList());
        }

        public List<ScriptProviderMetadata> getPolicies() {
            return Optional.ofNullable(descriptor.getProviders().get(ScriptProviderDescriptor.POLICIES)).orElse(Collections.emptyList());
        }
    }
}
