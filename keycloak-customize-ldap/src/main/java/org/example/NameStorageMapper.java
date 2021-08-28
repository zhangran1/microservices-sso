package org.example;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;

public class NameStorageMapper extends AbstractLDAPStorageMapper {

  public NameStorageMapper(ComponentModel mapperModel,
      LDAPStorageProvider ldapProvider) {
    super(mapperModel, ldapProvider);
  }


  @Override
  public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm,
      boolean isCreate) {

  }

  @Override
  public void onRegisterUserToLDAP(LDAPObject ldapUser, UserModel localUser, RealmModel realm) {

  }

  @Override
  public UserModel proxy(LDAPObject ldapUser, UserModel delegate, RealmModel realm) {
    return null;
  }

  @Override
  public void beforeLDAPQuery(LDAPQuery query) {

  }
}

