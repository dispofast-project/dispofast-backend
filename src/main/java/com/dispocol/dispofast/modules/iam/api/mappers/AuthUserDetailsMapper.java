package com.dispocol.dispofast.modules.iam.api.mappers;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dispocol.dispofast.modules.iam.domain.AppUser;

import io.jsonwebtoken.lang.Collections;

@Component
public class AuthUserDetailsMapper {
    
    public static UserDetails toUserDetails(AppUser user) {

        Collection<GrantedAuthority> authorities = Optional.ofNullable(user.getRoles())
            .orElse(Collections.emptySet())
            .stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
        
        return User.builder()
            .username(user.getEmail())
            .password(user.getPasswordHash())
            .authorities(authorities)
            .build();
    }
}
