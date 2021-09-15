## Sample Command to generate certificates

### Root Cert
```shell
openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt

PEM pass phrase: zenika

Country Name (2 letter code) [AU]:SG
State or Province Name (full name) [Some-State]:Singapore
Locality Name (eg, city) []:city
Organization Name (eg, company) [Internet Widgits Pty Ltd]:Zenika Pte Ltd
Organizational Unit Name (eg, section) []:certificates
Common Name (e.g. server FQDN or YOUR name) []:zenika-ca
Email Address []:zenika-ca@zenika.com
```


### Keycloak Host Certs

```shell
openssl req -new -newkey rsa:4096 -keyout localhost.key -out localhost.csr -nodes



Country Name (2 letter code) [AU]:SG
State or Province Name (full name) [Some-State]:Singapore
Locality Name (eg, city) []:city
Organization Name (eg, company) [Internet Widgits Pty Ltd]:Zenika-Demo Pte Ltd
Organizational Unit Name (eg, section) []:poc            
Common Name (e.g. server FQDN or YOUR name) []:keycloak-server
Email Address []:keycloak-server@zenika.com

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:keycloak-demo
An optional company name []:
```

#### Sign Host Cert
```shell
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in localhost.csr -out localhost.crt -days 365 -CAcreateserial -extfile localhost.ext
```


### Client Cert

```shell
openssl req -new -newkey rsa:4096 -nodes -keyout zhangran.key -out zhangran.csr

Country Name (2 letter code) [AU]:Singapore
string is too long, it needs to be no more than 2 bytes long
Country Name (2 letter code) [AU]:SG
State or Province Name (full name) [Some-State]:Singapore
Locality Name (eg, city) []:city
Organization Name (eg, company) [Internet Widgits Pty Ltd]:Zenika-POC Pte Ltd
Organizational Unit Name (eg, section) []:dev     
Common Name (e.g. server FQDN or YOUR name) []:zhangran
Email Address []:zhang.ran@zenika.com

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:zhangran-zenika
An optional company name []:
```

#### Sign Client Cert
```shell
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in zhangran.csr -out zhangran.crt -days 365 -CAcreateserial
enterpass phrase for rootCA: zenika
```

#### Import client key and crt in keystore to create the "certificate" to be used in the browser
```shell
openssl pkcs12 -export -out zhangran1.p12 -name "zhangran1" -inkey zhangran1.key -in zhangran1.crt
export password:zhangran1-zenika
```
