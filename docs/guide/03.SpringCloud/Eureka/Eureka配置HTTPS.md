## 生成自签名 SSL 证书

### 创建密钥库

用于密钥库的两种最常见的格式是 JKS（一种特定于 Java 的专有格式）和 PKCS12（一种行业标准格式）。 JKS 曾经是默认选择，但从 Java
9 开始，推荐使用 PKCS12 格式。

创建 **PKCS12 密钥库**

```bash
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore server.p12 -storepass changeit -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=CN" -ext "san=DNS:localhost,DNS:127.0.0.1,IP:127.0.0.1" -validity 3650
```

参数介绍：

- genkeypair：生成密钥对；
- alias：我们正在生成的项目的别名；
- keyalg：生成密钥对的密码算法；
- keysize：密钥的大小；
- keypass：密钥密码。不指定时，密钥库的密码同时为密钥密码
- storetype：密钥库的类型；
- keystore：密钥库的名称；
- validity：有效期天数；
- storepass：密钥库的密码。
- -dname 密钥的发行者
- -ext X.509 扩展

生成客户端证书：

```bash
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore client.p12 -storepass changeit -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=CN" -ext "san=DNS:localhost,DNS:127.0.0.1,IP:127.0.0.1" -validity 3650
```

### 导出证书

下面分别导出两个p12的证书：

```bash
keytool -exportcert -alias server -storepass changeit -keystore server.p12 -file server.crt

keytool -exportcert -alias client -storepass changeit -keystore client.p12 -file client.crt
```

### 双向信任证书

服务端信任客户端的证书：

```bash
keytool -import -alias client -file client.crt -keystore trustserver.p12
```

此步骤会生成信任证书 trustserver.p12 文件， 文件存放需要信任的公钥证书，如客户端证书（也可以将 keystore 值改为服务器密钥库，即
server.p12。此时的 server.p12 就同时是服务的密钥库和信任库）。

```bash
keytool -import -alias client -file client.crt -keystore server.p12
```

客户端信任服务端的证书：

```bash
keytool -import -alias server -file server.crt -keystore client.p12
```

参考：

- [HTTPS与数字证书（4）-keytool制作证书并配置](https://juejin.cn/post/7148247783882326053)

### 客户端证书导入浏览器

将 client.p12 导入浏览器证书，并设置我始终信任。

## Eureka Server配置

把生成的server.p12放到Maven工程的resources目录下

```yaml
---
spring.config.activate.on-profile: https

server:
  ssl:
    enabled: true
    client-auth: need
    key-store: classpath:server.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: server
    trust-store: classpath:server.p12
    trust-store-password: changeit
    trust-key-store-type: PKCS12

eureka:
  instance:
    securePort: ${server.port}
    securePortEnabled: true
    nonSecurePortEnabled: false
    homePageUrl: https://${eureka.instance.hostname}:${server.port}/
    statusPageUrl: https://${eureka.instance.hostname}:${server.port}/
    healthCheckUrl: https://${eureka.instance.hostname}:${server.port}/health
  client:
    serviceUrl:
      defaultZone: https://${eureka.instance.hostname}:${server.port}/eureka/
```

## Eureka Client配置

把生成的client.p12放到Maven工程的resources目录下

```yaml
---
spring.config.activate.on-profile: https

eureka:
  client:
    securePortEnabled: true
    tls:
      enabled: true
      key-password: changeit
      key-store: classpath:client.p12
      key-store-password: changeit
      key-store-type: PKCS12
      trust-store: classpath:client.p12
      trust-store-password: changeit
      trust-store-type: PKCS12
    serviceUrl:
      defaultZone: https://localhost:8761/eureka/
```

