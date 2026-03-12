package com.dispocol.dispofast.modules.iam.infra.security;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

  @Autowired private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    AppUser user =
        userRepository
            .findByEmailIgnoreCase(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + username));

    String[] permissions =
        user.getPermissions().stream()
            .map(permission -> permission.getPermission().getName().replace("PERMISSION_", ""))
            .toArray(String[]::new);

    return User.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .roles(permissions)
        .build();
  }
}
