Authlete Library for Jakarta (Java)
==================================

Overview
--------

This library provides utility classes to make it easy for developers
to implement an authorization server which supports [OAuth 2.0][1] and
[OpenID Connect][2] and a resource server.

[Authlete][7] is a cloud service that provides an implementation of OAuth
2.0 & OpenID Connect ([overview][8]). You can build a _DB-less_ authorization
server by using Authlete because authorization data (e.g. access tokens),
settings of authorization servers and settings of client applications are
stored in the Authlete server on cloud.

[java-oauth-server][3] is an authorization server implementation which
uses this library. It implements not only an authorization endpoint and
a token endpoint but also a JWK Set endpoint, a configuration endpoint
and a revocation endpoint. [java-resource-server][19] is a resource
server implementation which also uses this library. It supports a
[userinfo endpoint][20] defined in [OpenID Connect Core 1.0][13] and
includes an example of a protected resource endpoint, too. Use these
reference implementations as a starting point of your own implementations
of an authorization server and a resource server.


License
-------

  Apache License, Version 2.0


Maven
-----

```xml
<dependency>
    <groupId>com.authlete</groupId>
    <artifactId>authlete-java-jakarta</artifactId>
    <version>${authlete-java-jakarta.version}</version>
</dependency>
```

Please refer to the [CHANGES.md](CHANGES.md) file to know the latest version
to write in place of `${authlete-java-jaxrs.version}`.

Source Code
-----------

  <code>https://github.com/authlete/authlete-java-jakarta</code>


JavaDoc
-------

  <code>https://authlete.github.io/authlete-java-jakarta</code>