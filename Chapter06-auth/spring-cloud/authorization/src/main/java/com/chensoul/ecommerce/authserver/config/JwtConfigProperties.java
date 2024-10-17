package com.chensoul.ecommerce.authserver.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@EnableConfigurationProperties(JwtConfigProperties.class)
@Component
@Getter
@Setter
public class JwtConfigProperties {
  private RSAPublicKey publicKey;
  private RSAPrivateKey privateKey;
}