# microservices-sso

This repository contains experimentation code to validate Keycloak features.

## Getting Started

To Launch Keycloak, Keycloak-internal (can be used for token exchagne) Ldap1 and Ldap2 refer document in [docker-compose folder](docker-compose/README.md)

To Launch Microservices applications refer document in [Full Integration](full-integation/README.md)

To build custom Service Provider Interface [Keycloak Customize SPI](keycloak-customize-ldap)

To generate X509-Scripts, refer [X-509 Scripts](x509-scripts/README.md)


## References
1. [Create X.509 certificates](https://gist.github.com/dasniko/b1aa115fd9078372b03c7a9f7e9ec189)
2. [Keycloak Docker Demo](//github.com/thomasdarimont/keycloak-docker-demo)
3. [Keycloak Provider](https://github.com/keycloak/keycloak/blob/master/server-spi/src/main/java/org/keycloak/provider/Spi.java)

## Credit
Special thanks to the following peoples whom provide great support for this project:
* [Jérôme MARCHAND](https://github.com/jermarchand)
* [Timothee Dufresne](https://www.linkedin.com/in/timotheedufresne/)
* Thanks for the client whom sponsor this project

If you would like engage us for Keycloak related professional services, you may contact me or [Timothee Dufresne](https://www.linkedin.com/in/timotheedufresne/)

