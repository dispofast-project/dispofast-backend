package com.dispocol.dispofast.modules.iam.infra.security.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.dispocol.dispofast.modules.iam.infra.security.JWTProvider;
import com.dispocol.dispofast.modules.iam.infra.security.UserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    private final JWTProvider jwtProvider;
    private final UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                
        String token = extractToken(request);
        if (token == null) {
            log.debug("No Authorization Bearer token found for {} {}", request.getMethod(), request.getRequestURI());
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String username = jwtProvider.getUsernameFromToken(token);
                log.debug("Parsed JWT subject='{}' for {} {}", username, request.getMethod(), request.getRequestURI());

                if (username != null) {
                    UserDetails userDetails = userDetailService.loadUserByUsername(username);
                    if (jwtProvider.validateToken(token, userDetails)) {
                        log.debug("JWT validated for user '{}', granting authorities {}", userDetails.getUsername(), userDetails.getAuthorities());
                        UsernamePasswordAuthenticationToken authenticationToken = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                log.warn("JWT processing failed for {} {}: {}", request.getMethod(), request.getRequestURI(), e.toString());
                handlerExceptionResolver.resolveException(request, response, null, e);
                SecurityContextHolder.clearContext();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }


}