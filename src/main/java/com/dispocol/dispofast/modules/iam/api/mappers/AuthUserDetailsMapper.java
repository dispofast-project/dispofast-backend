package com.dispocol.dispofast.modules.iam.api.mappers;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import io.jsonwebtoken.lang.Collections;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUserDetailsMapper {

  public UserDetails toUserDetails(AppUser user) {

    Collection<GrantedAuthority> authorities =
        Optional.ofNullable(user.getPermissions()).orElse(Collections.emptySet()).stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission().getName()))
            .collect(Collectors.toList());

    return User.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .authorities(authorities)
        .build();
  }
}
