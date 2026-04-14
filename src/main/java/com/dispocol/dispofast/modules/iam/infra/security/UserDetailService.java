package com.dispocol.dispofast.modules.iam.infra.security;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser user =
        userRepository
            .findByEmailIgnoreCase(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + username));

    List<GrantedAuthority> authorities = new ArrayList<>();

    // Roles as ROLE_X (for hasRole() checks in IAM)
    user.getRoles()
        .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

    // Effective permissions from roles + user-level overrides (for hasAuthority() checks)
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
