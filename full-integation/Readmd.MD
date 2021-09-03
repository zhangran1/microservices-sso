#### Keycloak settings

```xml
keycloak.auth-server-url=http://localhost:8080/auth
keycloak.realm=user-application-role-mapping
```


#### OIDC single signon
- microservcies-frontend-springboot
- microservices-frontend-js
After user login from any of the above app, user is able to be automatically sign in another app.
  

#### Oauth2
- microservcies-frontend-springboot
- microservices-backend-springboot
After user with Admin access right login microservices-frontend-springboot, oauth call will be made to microservices-backend-springboot
  ```
  http://localhost:8019/secured/user
  ```
