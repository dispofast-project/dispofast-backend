package com.dispocol.dispofast.modules.iam.api.mappers;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUserDetailsMapper {

  public UserDetails toUserDetails(AppUser user) {

    List<GrantedAuthority> authorities = new ArrayList<>();

    // Roles como ROLE_X (para hasRole())
    user.getRoles()
        .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

    // Permisos efectivos: rol + overrides de usuario (para hasAuthority())
    user.resolveEffectivePermissionNames()
        .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

    return User.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .disabled(!user.isActive())
        .accountExpired(false)
        .credentialsExpired(false)
        .accountLocked(false)
        .authorities(authorities)
        .build();
  }
}
