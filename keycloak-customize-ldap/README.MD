This folder contains Keycloak customize Ldap provider

Compatible Keycloak Version: 15.0.1

Build Script
```shell
mvn clean install
```

How to deployment:
Place the **custom-ldap-provider.jar** under \keycloak\standalone\deployment folder.

### Important point 
After create custom ldap federation, copy and provider id and append to the following url to access custom 
LDAP federater's mapper page. Due to the limitation, the mapper is not able to appear automatically.

```shell
https://localhost:8443/auth/admin/master/console/#/realms/x509/ldap-mappers/{provider id from custom spi}
```
