#### Keycloak settings

```xml
keycloak.auth-server-url=https://localhost:8443/auth
keycloak.realm=x509
```


#### OIDC Single Sign On
- microservcies-frontend-springboot
- microservices-frontend-js
After user login from any of the above app, user is able to be automatically sign in another app.
  

#### Oauth2
- microservcies-frontend-springboot
- microservices-backend-springboot
After user with Admin access right login microservices-frontend-springboot, oauth call will be made to microservices-backend-springboot
  ```
  https://localhost:8019/secured/user
  ```
