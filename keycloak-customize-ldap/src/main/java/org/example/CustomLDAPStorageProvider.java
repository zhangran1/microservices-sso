package org.example;

import javax.ejb.Remove;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.store.ldap.LDAPIdentityStore;


public class CustomLDAPStorageProvider extends LDAPStorageProvider {

  private static final Logger logger = Logger.getLogger(CustomLDAPStorageProvider.class);

  public CustomLDAPStorageProvider(CustomLDAPStorageProviderFactory factory, KeycloakSession session,
      ComponentModel model, LDAPIdentityStore ldapIdentityStore) {
    super(factory, session, model, ldapIdentityStore);
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

  @Remove
  @Override
  public void close() {
    // according to
    // https://www.keycloak.org/docs/latest/server_development/#leveraging-java-ee
  }

}
