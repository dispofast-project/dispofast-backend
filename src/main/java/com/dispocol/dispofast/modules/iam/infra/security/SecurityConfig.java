package com.dispocol.dispofast.modules.iam.infra.security;

import com.dispocol.dispofast.modules.iam.infra.security.config.JwtAuthFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  @Autowired(required = false)
  @Qualifier("customAuthenticationEntryPoint")
  private AuthenticationEntryPoint authEntryPoint;

  private final ObjectProvider<JwtAuthFilter> jwtAuthFilterProvider;
  
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
          .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
          .authorizeHttpRequests(auth -> auth
              // Permit all preflight requests
              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
              // Permit actuator health and info endpoints
              .requestMatchers("/actuator/health", "/actuator/info").permitAll()
              // Permit Swagger UI resources
              .requestMatchers(
                  "/v3/api-docs/**", 
                  "/swagger-ui/**", 
                  "/swagger-ui.html"
              ).permitAll()
              // Permit authentication endpoints
              .requestMatchers("/auth/**").permitAll()
              .anyRequest().authenticated()
          );

    JwtAuthFilter jwtFilter = jwtAuthFilterProvider.getIfAvailable();
    if (jwtFilter != null) {
      log.info("Registering JwtAuthFilter before UsernamePasswordAuthenticationFilter");
      http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    } else {
      log.warn("JwtAuthFilter bean not available; requests will not be authenticated via JWT");
    }

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Adjust origins as needed for your frontend(s)
    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
