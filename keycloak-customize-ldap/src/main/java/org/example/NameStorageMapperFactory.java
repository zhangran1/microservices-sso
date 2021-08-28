package org.example;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;
import org.keycloak.storage.ldap.mappers.LDAPStorageMapper;

public class NameStorageMapperFactory extends AbstractLDAPStorageMapperFactory {
  public static final String PROVIDER_ID = "zenika-ldap-mapper";


  @Override
  public void init(Config.Scope config) {
  }

  @Override
  public LDAPStorageMapper create(KeycloakSession session, ComponentModel model) {
    // LDAPStorageProvider is in the session already as mappers are always called from it
    String ldapProviderModelId = model.getParentId();
    LDAPStorageProvider ldapProvider = (LDAPStorageProvider) session.getAttribute(ldapProviderModelId);

    return createMapper(model, ldapProvider);
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  // Used just by LDAPFederationMapperBridge.
  protected AbstractLDAPStorageMapper createMapper(ComponentModel mapperModel,
      LDAPStorageProvider federationProvider) {
    return new NameStorageMapper(mapperModel, federationProvider);
  }


  public static ProviderConfigProperty createConfigProperty(String name, String label, String helpText, String type, List<String> options) {
    ProviderConfigProperty configProperty = new ProviderConfigProperty();
    configProperty.setName(name);
    configProperty.setLabel(label);
    configProperty.setHelpText(helpText);
    configProperty.setType(type);
    configProperty.setOptions(options);
    return configProperty;
  }

  protected void checkMandatoryConfigAttribute(String name, String displayName, ComponentModel mapperModel) throws ComponentValidationException {
    String attrConfigValue = mapperModel.getConfig().getFirst(name);
    if (attrConfigValue == null || attrConfigValue.trim().isEmpty()) {
      throw new ComponentValidationException("Missing configuration for '" + displayName + "'");
    }
  }
}

