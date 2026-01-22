package com.dispocol.dispofast.modules.iam.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class InMemoryUserConfig {

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin@local")
                .password("{noop}admin") // SOLO dev. Luego BCrypt.
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
