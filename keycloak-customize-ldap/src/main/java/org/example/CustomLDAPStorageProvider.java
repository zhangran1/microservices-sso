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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.ejb.Remove;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialAuthentication;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.store.ldap.LDAPIdentityStore;
import org.keycloak.storage.ldap.mappers.LDAPMappersComparator;
import org.keycloak.storage.ldap.mappers.LDAPStorageMapper;
import org.keycloak.storage.user.ImportedUserValidation;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;


public class CustomLDAPStorageProvider extends LDAPStorageProvider implements UserStorageProvider,
    CredentialInputValidator,
    CredentialInputUpdater.Streams,
    CredentialAuthentication,
    UserLookupProvider.Streams,
    UserRegistrationProvider,
    UserQueryProvider.Streams,
    ImportedUserValidation {

  private static final Logger logger = Logger.getLogger(CustomLDAPStorageProvider.class);

  private static final int DEFAULT_MAX_RESULTS = Integer.MAX_VALUE >> 1;

  private LDAPMappersComparator ldapMappersComparator;

  public CustomLDAPStorageProvider(CustomLDAPStorageProviderFactory factory, KeycloakSession session,
      ComponentModel model, LDAPIdentityStore ldapIdentityStore) {
    super(factory, session, model, ldapIdentityStore);
    ldapMappersComparator = new LDAPMappersComparator(getLdapIdentityStore().getConfig());
  }

  public LDAPIdentityStore getLdapIdentityStore() {
    return this.ldapIdentityStore;
  }

  @Override
  public UserModel validate(RealmModel realm, UserModel local) {
    try {
      logger.error("#######   VALIDATE USER");

      return super.validate(realm, local);
    } catch (Exception e) {
      logger.error("####### ERROR   VALIDATE USER  ");

      logger.error(e);
      return null;
    }
  }

  @Override
  public boolean validPassword(RealmModel realm, UserModel user, String password) {
    try {
      logger.error("#######   VALIDATE password");
      return super.validPassword(realm, user, password);
    } catch (Exception e) {
      logger.error("####### FOR DEMO PURPOUSE ONLY PASSWORDS WILL ALLWAYS BE CORRECT");

      return true;
    }
  }

  @Override
  protected LDAPObject loadAndValidateUser(RealmModel realm, UserModel local) {
    try {
      logger.error("#######   LOAD AND VALIDATE USER ");
      return super.loadAndValidateUser(realm, local);

    } catch (Exception e) {
      logger.error("####### Error LOAD AND VALIDATE USER  ");

      LDAPObject cached = new LDAPObject();
      cached.setUuid(local.getId());
      return cached;
    }
  }

  @Override
  public LDAPObject loadLDAPUserByUsername(RealmModel realm, String username) {
    logger.error("####### LOAD BY USER MANE  " + username);
    LDAPObject user = super.loadLDAPUserByUsername(realm, username);

    return user;
  }

  @Override
  public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {

    int first = firstResult == null ? 0 : firstResult;
    int max = maxResults == null ? DEFAULT_MAX_RESULTS : maxResults;
    logger.infof("***********Custom LDAP Storage Provider group= %s", group.toString());
    return realm.getComponentsStream(model.getId(), LDAPStorageMapper.class.getName())
        .sorted(ldapMappersComparator.sortAsc())
        .map(mapperModel ->
            mapperManager.getMapper(mapperModel).getGroupMembers(realm, group, first, max))
        .filter(((Predicate<List>) List::isEmpty).negate())
        .map(List::stream)
        .findFirst().orElse(Stream.empty());
  }

  @Override
  public Stream<UserModel> getRoleMembersStream(RealmModel realm, RoleModel role, Integer firstResult, Integer maxResults) {

    logger.infof("***********Custom LDAP Storage Provider role= %s", role.toString());
    int first = firstResult == null ? 0 : firstResult;
    int max = maxResults == null ? DEFAULT_MAX_RESULTS : maxResults;

    return realm.getComponentsStream(model.getId(), LDAPStorageMapper.class.getName())
        .sorted(ldapMappersComparator.sortAsc())
        .map(mapperModel -> mapperManager.getMapper(mapperModel).getRoleMembers(realm, role, first, max))
        .filter(((Predicate<List>) List::isEmpty).negate())
        .map(List::stream)
        .findFirst().orElse(Stream.empty());
  }

  @Remove
  @Override
  public void close() {
    // according to
    // https://www.keycloak.org/docs/latest/server_development/#leveraging-java-ee
  }

}
