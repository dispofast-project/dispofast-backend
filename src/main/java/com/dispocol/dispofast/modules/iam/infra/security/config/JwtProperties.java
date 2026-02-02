package com.dispocol.dispofast.modules.iam.infra.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationSeconds) {
    public JwtProperties {
        if (secret == null || secret.isBlank() || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("JWT expirationSeconds must be greater than 0");
        }
    }
}
