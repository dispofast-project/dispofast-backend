package com.dispocol.dispofast.modules.iam.infra.security;

import com.dispocol.dispofast.modules.iam.infra.security.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JWTProvider {

  private final JwtProperties jwtProperties;
  private final SecretKey secretKey;

  public JWTProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
  }

  public String generateToken(UserDetails userDetails) {
    long nowMillis = System.currentTimeMillis();
    long expMillis = nowMillis + (jwtProperties.expirationSeconds() * 1000);

    List<String> authorities =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim("authorities", authorities)
        .issuedAt(new Date(nowMillis))
        .expiration(new Date(expMillis))
        .signWith(secretKey)
        .compact();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    String username = getUsernameFromToken(token);

    return (username.equals(userDetails.getUsername())
        && !getClaims(token).getExpiration().before(new Date()));
  }

  public Claims getClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public String getUsernameFromToken(String token) {
    return getClaims(token).getSubject();
  }
}
