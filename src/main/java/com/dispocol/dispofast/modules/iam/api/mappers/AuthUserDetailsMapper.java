package com.dispocol.dispofast.modules.iam.api.mappers;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dispocol.dispofast.modules.iam.domain.AppUser;

@Component
public class AuthUserDetailsMapper {
    
    public UserDetails toUserDetails(AppUser user) {
        return User.builder()
            .username(user.getEmail())
            .password(user.getPasswordHash())
            .roles(user.getRoles().toArray(new String[0]))
            .build();
    }
}
