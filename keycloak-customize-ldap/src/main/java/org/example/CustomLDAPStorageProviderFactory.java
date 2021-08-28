package org.example;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.keycloak.Config;
import org.keycloak.common.constants.KerberosConstants;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.LDAPConstants;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.storage.ldap.LDAPIdentityStoreRegistry;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.LDAPStorageProviderFactory;
import org.keycloak.storage.ldap.idm.store.ldap.LDAPIdentityStore;
import org.keycloak.storage.ldap.mappers.LDAPConfigDecorator;

public class CustomLDAPStorageProviderFactory extends LDAPStorageProviderFactory implements ServerInfoAwareProviderFactory {
  private LDAPIdentityStoreRegistry ldapStoreRegistry;

  @Override
  public String getId() {
    return "zenika-ldap";
  }

  @Override
  public void init(Config.Scope config) {
    this.ldapStoreRegistry = new LDAPIdentityStoreRegistry();
  }

  @Override
  public LDAPStorageProvider create(KeycloakSession session, ComponentModel model) {
    Map<ComponentModel, LDAPConfigDecorator> configDecorators = getLDAPConfigDecorators(session, model);

    LDAPIdentityStore ldapIdentityStore = this.ldapStoreRegistry.getLdapStore(session, model, configDecorators);
    return new CustomLDAPStorageProvider(this, session, model, ldapIdentityStore);
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    List<ProviderConfigProperty> props = new LinkedList<>();
    props.add(new ProviderConfigProperty(LDAPConstants.EDIT_MODE, LDAPConstants.EDIT_MODE, LDAPConstants.EDIT_MODE,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(UserStorageProviderModel.IMPORT_ENABLED,
        UserStorageProviderModel.IMPORT_ENABLED, UserStorageProviderModel.IMPORT_ENABLED,
        ProviderConfigProperty.BOOLEAN_TYPE, "true"));
    props.add(new ProviderConfigProperty(LDAPConstants.SYNC_REGISTRATIONS, LDAPConstants.SYNC_REGISTRATIONS,
        LDAPConstants.SYNC_REGISTRATIONS, ProviderConfigProperty.BOOLEAN_TYPE, "false"));
    props.add(new ProviderConfigProperty(LDAPConstants.VENDOR, LDAPConstants.VENDOR, LDAPConstants.VENDOR,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.USE_PASSWORD_MODIFY_EXTENDED_OP,
        LDAPConstants.USE_PASSWORD_MODIFY_EXTENDED_OP, LDAPConstants.USE_PASSWORD_MODIFY_EXTENDED_OP,
        ProviderConfigProperty.BOOLEAN_TYPE, "false"));
    props.add(
        new ProviderConfigProperty(LDAPConstants.USERNAME_LDAP_ATTRIBUTE, LDAPConstants.USERNAME_LDAP_ATTRIBUTE,
            LDAPConstants.USERNAME_LDAP_ATTRIBUTE, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.RDN_LDAP_ATTRIBUTE, LDAPConstants.RDN_LDAP_ATTRIBUTE,
        LDAPConstants.RDN_LDAP_ATTRIBUTE, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.UUID_LDAP_ATTRIBUTE, LDAPConstants.UUID_LDAP_ATTRIBUTE,
        LDAPConstants.UUID_LDAP_ATTRIBUTE, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.USER_OBJECT_CLASSES, LDAPConstants.USER_OBJECT_CLASSES,
        LDAPConstants.USER_OBJECT_CLASSES, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_URL, LDAPConstants.CONNECTION_URL,
        LDAPConstants.CONNECTION_URL, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.USERS_DN, LDAPConstants.USERS_DN, LDAPConstants.USERS_DN,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.AUTH_TYPE, LDAPConstants.AUTH_TYPE, LDAPConstants.AUTH_TYPE,
        ProviderConfigProperty.STRING_TYPE, "simple"));
    props.add(new ProviderConfigProperty(LDAPConstants.START_TLS, LDAPConstants.START_TLS, LDAPConstants.START_TLS,
        ProviderConfigProperty.BOOLEAN_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.BIND_DN, LDAPConstants.BIND_DN, LDAPConstants.BIND_DN,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.BIND_CREDENTIAL, LDAPConstants.BIND_CREDENTIAL,
        LDAPConstants.BIND_CREDENTIAL, ProviderConfigProperty.PASSWORD, "", true));
    props.add(new ProviderConfigProperty(LDAPConstants.CUSTOM_USER_SEARCH_FILTER,
        LDAPConstants.CUSTOM_USER_SEARCH_FILTER, LDAPConstants.CUSTOM_USER_SEARCH_FILTER,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.SEARCH_SCOPE, LDAPConstants.SEARCH_SCOPE,
        LDAPConstants.SEARCH_SCOPE, ProviderConfigProperty.STRING_TYPE, "1"));
    props.add(new ProviderConfigProperty(LDAPConstants.VALIDATE_PASSWORD_POLICY,
        LDAPConstants.VALIDATE_PASSWORD_POLICY, LDAPConstants.VALIDATE_PASSWORD_POLICY,
        ProviderConfigProperty.BOOLEAN_TYPE, "false"));
    props.add(new ProviderConfigProperty(LDAPConstants.TRUST_EMAIL, LDAPConstants.TRUST_EMAIL,
        LDAPConstants.TRUST_EMAIL, ProviderConfigProperty.BOOLEAN_TYPE, "false"));
    props.add(new ProviderConfigProperty(LDAPConstants.USE_TRUSTSTORE_SPI, LDAPConstants.USE_TRUSTSTORE_SPI,
        LDAPConstants.USE_TRUSTSTORE_SPI, ProviderConfigProperty.STRING_TYPE, "ldapsOnly"));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING, LDAPConstants.CONNECTION_POOLING,
        LDAPConstants.CONNECTION_POOLING, ProviderConfigProperty.BOOLEAN_TYPE, "true"));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING_AUTHENTICATION,
        LDAPConstants.CONNECTION_POOLING_AUTHENTICATION, LDAPConstants.CONNECTION_POOLING_AUTHENTICATION,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING_DEBUG,
        LDAPConstants.CONNECTION_POOLING_DEBUG, LDAPConstants.CONNECTION_POOLING_DEBUG,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING_INITSIZE,
        LDAPConstants.CONNECTION_POOLING_INITSIZE, LDAPConstants.CONNECTION_POOLING_INITSIZE,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING_MAXSIZE,
        LDAPConstants.CONNECTION_POOLING_MAXSIZE, LDAPConstants.CONNECTION_POOLING_MAXSIZE,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING_PREFSIZE,
        LDAPConstants.CONNECTION_POOLING_PREFSIZE, LDAPConstants.CONNECTION_POOLING_PREFSIZE,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING_PROTOCOL,
        LDAPConstants.CONNECTION_POOLING_PROTOCOL, LDAPConstants.CONNECTION_POOLING_PROTOCOL,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_POOLING_TIMEOUT,
        LDAPConstants.CONNECTION_POOLING_TIMEOUT, LDAPConstants.CONNECTION_POOLING_TIMEOUT,
        ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.CONNECTION_TIMEOUT, LDAPConstants.CONNECTION_TIMEOUT,
        LDAPConstants.CONNECTION_TIMEOUT, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.READ_TIMEOUT, LDAPConstants.READ_TIMEOUT,
        LDAPConstants.READ_TIMEOUT, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(LDAPConstants.PAGINATION, LDAPConstants.PAGINATION,
        LDAPConstants.PAGINATION, ProviderConfigProperty.BOOLEAN_TYPE, "true"));
    props.add(new ProviderConfigProperty(KerberosConstants.ALLOW_KERBEROS_AUTHENTICATION,
        KerberosConstants.ALLOW_KERBEROS_AUTHENTICATION, KerberosConstants.ALLOW_KERBEROS_AUTHENTICATION,
        ProviderConfigProperty.BOOLEAN_TYPE, "false"));
    props.add(new ProviderConfigProperty(KerberosConstants.SERVER_PRINCIPAL, KerberosConstants.SERVER_PRINCIPAL,
        KerberosConstants.SERVER_PRINCIPAL, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(KerberosConstants.KEYTAB, KerberosConstants.KEYTAB,
        KerberosConstants.KEYTAB, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(KerberosConstants.KERBEROS_REALM, KerberosConstants.KERBEROS_REALM,
        KerberosConstants.KERBEROS_REALM, ProviderConfigProperty.STRING_TYPE, ""));
    props.add(new ProviderConfigProperty(KerberosConstants.DEBUG, KerberosConstants.DEBUG, KerberosConstants.DEBUG,
        ProviderConfigProperty.BOOLEAN_TYPE, "false"));
    props.add(new ProviderConfigProperty(KerberosConstants.USE_KERBEROS_FOR_PASSWORD_AUTHENTICATION,
        KerberosConstants.USE_KERBEROS_FOR_PASSWORD_AUTHENTICATION,
        KerberosConstants.USE_KERBEROS_FOR_PASSWORD_AUTHENTICATION, ProviderConfigProperty.BOOLEAN_TYPE,
        "false"));
    props.add(new ProviderConfigProperty(KerberosConstants.SERVER_PRINCIPAL, KerberosConstants.SERVER_PRINCIPAL,
        KerberosConstants.SERVER_PRINCIPAL, ProviderConfigProperty.STRING_TYPE, ""));

    return props;
  }

  @Override
  public Map<String, String> getOperationalInfo() {
    Map<String, String> ret = new LinkedHashMap<>();
    ret.put("custom-ldap", "zenika-ldap");
    return ret;
  }

}
