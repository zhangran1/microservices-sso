/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.common.constants.KerberosConstants;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakSessionTask;
import org.keycloak.models.LDAPConstants;
import org.keycloak.models.ModelException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.UserCache;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.UserStorageProvider.EditMode;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.storage.ldap.LDAPConfig;
import org.keycloak.storage.ldap.LDAPIdentityStoreRegistry;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.LDAPStorageProviderFactory;
import org.keycloak.storage.ldap.LDAPUtils;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.idm.store.ldap.LDAPIdentityStore;
import org.keycloak.storage.ldap.mappers.FullNameLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.FullNameLDAPStorageMapperFactory;
import org.keycloak.storage.ldap.mappers.HardcodedLDAPAttributeMapper;
import org.keycloak.storage.ldap.mappers.HardcodedLDAPAttributeMapperFactory;
import org.keycloak.storage.ldap.mappers.LDAPConfigDecorator;
import org.keycloak.storage.ldap.mappers.LDAPMappersComparator;
import org.keycloak.storage.ldap.mappers.LDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.UserAttributeLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.UserAttributeLDAPStorageMapperFactory;
import org.keycloak.storage.ldap.mappers.msad.MSADUserAccountControlStorageMapperFactory;
import org.keycloak.storage.user.ImportSynchronization;
import org.keycloak.storage.user.SynchronizationResult;
import org.keycloak.utils.CredentialHelper;

public class CustomLDAPStorageProviderFactory extends LDAPStorageProviderFactory implements ServerInfoAwareProviderFactory, ImportSynchronization {
  private LDAPIdentityStoreRegistry ldapStoreRegistry;
  private static final Logger logger = Logger.getLogger(CustomLDAPStorageProviderFactory.class);
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

  // Best effort to create appropriate mappers according to our LDAP config
  @Override
  public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {

    LDAPConfig ldapConfig = new LDAPConfig(model.getConfig());

    boolean activeDirectory = ldapConfig.isActiveDirectory();

    // UserStorageProvider.EditMode editMode = ldapConfig.getEditMode();
    UserStorageProvider.EditMode editMode = EditMode.UNSYNCED;

    String readOnly = String.valueOf(editMode == UserStorageProvider.EditMode.READ_ONLY || editMode == UserStorageProvider.EditMode.UNSYNCED);
    String usernameLdapAttribute = ldapConfig.getUsernameLdapAttribute();
    boolean syncRegistrations = Boolean.valueOf(model.getConfig().getFirst(LDAPConstants.SYNC_REGISTRATIONS));

    String alwaysReadValueFromLDAP = String.valueOf(editMode== UserStorageProvider.EditMode.READ_ONLY || editMode== UserStorageProvider.EditMode.WRITABLE);

    ComponentModel mapperModel;

    mapperModel = KeycloakModelUtils
        .createComponentModel("username", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID, LDAPStorageMapper.class.getName(),
            UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, UserModel.USERNAME,
            UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, usernameLdapAttribute,
            UserAttributeLDAPStorageMapper.READ_ONLY, readOnly,
            UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, "false",
            UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "true");
    realm.addComponentModel(mapperModel);
    // CN is typically used as RDN for Active Directory deployments
    if (ldapConfig.getRdnLdapAttribute().equalsIgnoreCase(LDAPConstants.CN)) {

      if (usernameLdapAttribute.equalsIgnoreCase(LDAPConstants.CN)) {
        // For AD deployments with "cn" as username, we will map "givenName" to first name
        mapperModel = KeycloakModelUtils.createComponentModel("first name", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
            UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, UserModel.FIRST_NAME,
            UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, LDAPConstants.GIVENNAME,
            UserAttributeLDAPStorageMapper.READ_ONLY, readOnly,
            UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, alwaysReadValueFromLDAP,
            UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "true");

        realm.addComponentModel(mapperModel);

      } else {
        if (editMode == UserStorageProvider.EditMode.WRITABLE) {

          // For AD deployments with "sAMAccountName" as username and writable, we need to map "cn" as username as well (this is needed so we can register new users from KC into LDAP) and we will map "givenName" to first name.
          mapperModel = KeycloakModelUtils.createComponentModel("first name", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
              UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, UserModel.FIRST_NAME,
              UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, LDAPConstants.GIVENNAME,
              UserAttributeLDAPStorageMapper.READ_ONLY, readOnly,
              UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, alwaysReadValueFromLDAP,
              UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "true");
          realm.addComponentModel(mapperModel);

          mapperModel = KeycloakModelUtils.createComponentModel("username-cn", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
              UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, UserModel.USERNAME,
              UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, LDAPConstants.CN,
              UserAttributeLDAPStorageMapper.READ_ONLY, readOnly,
              UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, "false",
              UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "true");
          realm.addComponentModel(mapperModel);
        } else {

          // For read-only LDAP, we map "cn" as full name
          mapperModel = KeycloakModelUtils.createComponentModel("full name", model.getId(), FullNameLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
              FullNameLDAPStorageMapper.LDAP_FULL_NAME_ATTRIBUTE, LDAPConstants.CN,
              FullNameLDAPStorageMapper.READ_ONLY, readOnly,
              FullNameLDAPStorageMapper.WRITE_ONLY, "false");
          realm.addComponentModel(mapperModel);
        }
      }
    } else {
      mapperModel = KeycloakModelUtils.createComponentModel("first name", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
          UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, UserModel.FIRST_NAME,
          UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, LDAPConstants.CN,
          UserAttributeLDAPStorageMapper.READ_ONLY, readOnly,
          UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, alwaysReadValueFromLDAP,
          UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "true");

      realm.addComponentModel(mapperModel);
    }
    mapperModel = KeycloakModelUtils.createComponentModel("last name", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
        UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, UserModel.LAST_NAME,
        UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, LDAPConstants.SN,
        UserAttributeLDAPStorageMapper.READ_ONLY, readOnly,
        UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, alwaysReadValueFromLDAP,
        UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "true");
    realm.addComponentModel(mapperModel);

    mapperModel = KeycloakModelUtils.createComponentModel("email", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
        UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, UserModel.EMAIL,
        UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, LDAPConstants.EMAIL,
        UserAttributeLDAPStorageMapper.READ_ONLY, readOnly,
        UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, "false",
        UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "false");
    realm.addComponentModel(mapperModel);

    String createTimestampLdapAttrName = activeDirectory ? "whenCreated" : LDAPConstants.CREATE_TIMESTAMP;
    String modifyTimestampLdapAttrName = activeDirectory ? "whenChanged" : LDAPConstants.MODIFY_TIMESTAMP;

    // map createTimeStamp as read-only
    mapperModel = KeycloakModelUtils.createComponentModel("creation date", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
        UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, LDAPConstants.CREATE_TIMESTAMP,
        UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, createTimestampLdapAttrName,
        UserAttributeLDAPStorageMapper.READ_ONLY, "true",
        UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, alwaysReadValueFromLDAP,
        UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "false");
    realm.addComponentModel(mapperModel);

    // map modifyTimeStamp as read-only
    mapperModel = KeycloakModelUtils.createComponentModel("modify date", model.getId(), UserAttributeLDAPStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
        UserAttributeLDAPStorageMapper.USER_MODEL_ATTRIBUTE, LDAPConstants.MODIFY_TIMESTAMP,
        UserAttributeLDAPStorageMapper.LDAP_ATTRIBUTE, modifyTimestampLdapAttrName,
        UserAttributeLDAPStorageMapper.READ_ONLY, "true",
        UserAttributeLDAPStorageMapper.ALWAYS_READ_VALUE_FROM_LDAP, alwaysReadValueFromLDAP,
        UserAttributeLDAPStorageMapper.IS_MANDATORY_IN_LDAP, "false");
    realm.addComponentModel(mapperModel);

    // MSAD specific mapper for account state propagation
    if (activeDirectory) {
      mapperModel = KeycloakModelUtils.createComponentModel("MSAD account controls", model.getId(), MSADUserAccountControlStorageMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName());
      realm.addComponentModel(mapperModel);
    }
    String allowKerberosCfg = model.getConfig().getFirst(KerberosConstants.ALLOW_KERBEROS_AUTHENTICATION);
    if (Boolean.valueOf(allowKerberosCfg)) {
      CredentialHelper
          .setOrReplaceAuthenticationRequirement(session, realm, CredentialRepresentation.KERBEROS,
              AuthenticationExecutionModel.Requirement.ALTERNATIVE, AuthenticationExecutionModel.Requirement.DISABLED);
    }

    // In case that "Sync Registration" is ON and the LDAP v3 Password-modify extension is ON, we will create hardcoded mapper to create
    // random "userPassword" every time when creating user. Otherwise users won't be able to register and login
    if (!activeDirectory && syncRegistrations && ldapConfig.useExtendedPasswordModifyOp()) {
      mapperModel = KeycloakModelUtils.createComponentModel("random initial password", model.getId(), HardcodedLDAPAttributeMapperFactory.PROVIDER_ID,LDAPStorageMapper.class.getName(),
          HardcodedLDAPAttributeMapper.LDAP_ATTRIBUTE_NAME, LDAPConstants.USER_PASSWORD_ATTRIBUTE,
          HardcodedLDAPAttributeMapper.LDAP_ATTRIBUTE_VALUE, HardcodedLDAPAttributeMapper.RANDOM_ATTRIBUTE_VALUE);
      realm.addComponentModel(mapperModel);
    }
  }

  @Override
  public SynchronizationResult sync(KeycloakSessionFactory sessionFactory, String realmId, UserStorageProviderModel model) {
    syncMappers(sessionFactory, realmId, model);

    logger.infof("Sync all users from LDAP to local store: realm: %s, federation provider: %s", realmId, model.getName());

    try (LDAPQuery userQuery = createQuery(sessionFactory, realmId, model)) {
      SynchronizationResult syncResult = syncImpl(sessionFactory, userQuery, realmId, model);

      // TODO: Remove all existing keycloak users, which have federation links, but are not in LDAP. Perhaps don't check users, which were just added or updated during this sync?

      logger.infof("Sync all users finished: %s", syncResult.getStatus());
      return syncResult;
    }
  }

  protected SynchronizationResult syncImpl(KeycloakSessionFactory sessionFactory, LDAPQuery userQuery, final String realmId, final ComponentModel fedModel) {
    final SynchronizationResult syncResult = new SynchronizationResult();

    LDAPConfig ldapConfig = new LDAPConfig(fedModel.getConfig());
    boolean pagination = ldapConfig.isPagination();
    if (pagination) {
      int pageSize = ldapConfig.getBatchSizeForSync();

      boolean nextPage = true;
      while (nextPage) {
        userQuery.setLimit(pageSize);
        final List<LDAPObject> users = userQuery.getResultList();
        nextPage = userQuery.getPaginationContext().hasNextPage();
        SynchronizationResult currentPageSync = importLdapUsers(sessionFactory, realmId, fedModel, users);
        syncResult.add(currentPageSync);
      }
    } else {
      // LDAP pagination not available. Do everything in single transaction
      final List<LDAPObject> users = userQuery.getResultList();
      SynchronizationResult currentSync = importLdapUsers(sessionFactory, realmId, fedModel, users);
      syncResult.add(currentSync);
    }

    return syncResult;
  }

  protected SynchronizationResult importLdapUsers(KeycloakSessionFactory sessionFactory, final String realmId, final ComponentModel fedModel, List<LDAPObject> ldapUsers) {
    final SynchronizationResult syncResult = new SynchronizationResult();

    class BooleanHolder {
      private boolean value = true;
    }
    final BooleanHolder exists = new BooleanHolder();

    for (final LDAPObject ldapUser : ldapUsers) {

      try {

        // Process each user in it's own transaction to avoid global fail
        KeycloakModelUtils.runJobInTransaction(sessionFactory, new KeycloakSessionTask() {

          @Override
          public void run(KeycloakSession session) {
            LDAPStorageProvider ldapFedProvider = (LDAPStorageProvider)session.getProvider(UserStorageProvider.class, fedModel);
            RealmModel currentRealm = session.realms().getRealm(realmId);
            session.getContext().setRealm(currentRealm);

            String username = LDAPUtils.getUsername(ldapUser, ldapFedProvider.getLdapIdentityStore().getConfig());
            exists.value = true;
            LDAPUtils.checkUuid(ldapUser, ldapFedProvider.getLdapIdentityStore().getConfig());
            UserModel currentUserLocal = session.userLocalStorage().getUserByUsername(currentRealm, username);
            Optional<UserModel> userModelOptional = session.userLocalStorage()
                .searchForUserByUserAttributeStream(currentRealm, LDAPConstants.LDAP_ID, ldapUser.getUuid())
                .findFirst();
            if (!userModelOptional.isPresent() && currentUserLocal == null) {
              // Add new user to Keycloak
              exists.value = false;
              ldapFedProvider.importUserFromLDAP(session, currentRealm, ldapUser);
              syncResult.increaseAdded();

            } else {
              UserModel currentUser = userModelOptional.isPresent() ? userModelOptional.get() : currentUserLocal;
              if ((fedModel.getId().equals(currentUser.getFederationLink())) && (ldapUser.getUuid().equals(currentUser.getFirstAttribute(LDAPConstants.LDAP_ID)))) {

                // Update keycloak user
                LDAPMappersComparator ldapMappersComparator = new LDAPMappersComparator(ldapFedProvider.getLdapIdentityStore().getConfig());
                currentRealm.getComponentsStream(fedModel.getId(), LDAPStorageMapper.class.getName())
                    .sorted(ldapMappersComparator.sortDesc())
                    .forEachOrdered(mapperModel -> {
                      LDAPStorageMapper ldapMapper = ldapFedProvider.getMapperManager().getMapper(mapperModel);
                      ldapMapper.onImportUserFromLDAP(ldapUser, currentUser, currentRealm, false);
                    });

                UserCache userCache = session.userCache();
                if (userCache != null) {
                  userCache.evict(currentRealm, currentUser);
                }
                logger.debugf("Updated user from LDAP: %s", currentUser.getUsername());
                syncResult.increaseUpdated();
              } else {
                /* Default behavior:
                   When there is an existing user existed in the Keycloak, keycloak will show user exist
                   and no further action will be taken.

                   This script changed the logic of how this part of the code behave, once there is
                   an exising user with same uuid found in keycloak, the onImportUserFromLDAP will
                   be called, isCreate shall be set to true since the user exists in keycloak, only
                   groups will be added to keycloak and group information for the user will be updated as well.
                 */
                logger.infof("@@@@@@@@@@ Please change the logic from here to customize the user import");
                logger.infof("@@@@@@@@@@ current user =%s", currentUser.getId());
                logger.infof("@@@@@@@@@@@@@@ ldap user uuid=%s", ldapUser.getUuid());
                //logger.warnf("User with ID '%s' is not updated during sync as he already exists in Keycloak database but is not linked to federation provider '%s'", ldapUser.getUuid(), fedModel.getName());

                // Update keycloak user
                LDAPMappersComparator ldapMappersComparator = new LDAPMappersComparator(ldapFedProvider.getLdapIdentityStore().getConfig());
                currentRealm.getComponentsStream(fedModel.getId(), LDAPStorageMapper.class.getName())
                    .sorted(ldapMappersComparator.sortDesc())
                    .forEachOrdered(mapperModel -> {
                      LDAPStorageMapper ldapMapper = ldapFedProvider.getMapperManager().getMapper(mapperModel);
                      ldapMapper.onImportUserFromLDAP(ldapUser, currentUser, currentRealm, true);
                    });

                UserCache userCache = session.userCache();
                if (userCache != null) {
                  userCache.evict(currentRealm, currentUser);
                }
                logger.debugf("Updated user from LDAP: %s", currentUser.getUsername());
                syncResult.increaseUpdated();

//                syncResult.increaseFailed();
              }
            }
          }

        });
      } catch (ModelException me) {
        logger.error("Failed during import user from LDAP", me);
        syncResult.increaseFailed();

        // Remove user if we already added him during this transaction
        if (!exists.value) {
          KeycloakModelUtils.runJobInTransaction(sessionFactory, new KeycloakSessionTask() {

            @Override
            public void run(KeycloakSession session) {
              LDAPStorageProvider ldapFedProvider = (LDAPStorageProvider)session.getProvider(UserStorageProvider.class, fedModel);
              RealmModel currentRealm = session.realms().getRealm(realmId);
              session.getContext().setRealm(currentRealm);

              String username = null;
              try {
                username = LDAPUtils.getUsername(ldapUser, ldapFedProvider.getLdapIdentityStore().getConfig());
              } catch (ModelException ignore) {
              }

              if (username != null) {
                UserModel existing = session.userLocalStorage().getUserByUsername(currentRealm, username);
                if (existing != null) {
                  UserCache userCache = session.userCache();
                  if (userCache != null) {
                    userCache.evict(currentRealm, existing);
                  }
                  session.userLocalStorage().removeUser(currentRealm, existing);
                }
              }
            }

          });
        }
      }
    }

    return syncResult;
  }


  private LDAPQuery createQuery(KeycloakSessionFactory sessionFactory, final String realmId, final ComponentModel model) {
    class QueryHolder {
      LDAPQuery query;
    }

    final QueryHolder queryHolder = new QueryHolder();
    KeycloakModelUtils.runJobInTransaction(sessionFactory, new KeycloakSessionTask() {

      @Override
      public void run(KeycloakSession session) {
        session.getContext().setRealm(session.realms().getRealm(realmId));

        LDAPStorageProvider ldapFedProvider = (LDAPStorageProvider)session.getProvider(UserStorageProvider.class, model);
        RealmModel realm = session.realms().getRealm(realmId);
        queryHolder.query = LDAPUtils.createQueryForUserSearch(ldapFedProvider, realm);
      }

    });
    return queryHolder.query;
  }

  @Override
  public Map<String, String> getOperationalInfo() {
    Map<String, String> ret = new LinkedHashMap<>();
    ret.put("custom-ldap", "zenika-ldap");
    return ret;
  }

}
