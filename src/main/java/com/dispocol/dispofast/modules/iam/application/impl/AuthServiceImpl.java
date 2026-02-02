package com.dispocol.dispofast.modules.iam.application.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dispocol.dispofast.modules.iam.api.dtos.LoginRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.LoginResponseDTO;
import com.dispocol.dispofast.modules.iam.api.mappers.AuthUserDetailsMapper;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapper;
import com.dispocol.dispofast.modules.iam.application.interfaces.AuthService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.iam.infra.security.JWTProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JWTProvider jwtProvider;

    private final AuthUserDetailsMapper authUserDetailsMapper;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
       AppUser user = 
            userRepository.findByEmailIgnoreCase(loginRequest.getEmail().trim())
                            .orElseThrow(() -> 
                            new IllegalArgumentException("Invalid email or password"));
        
        if(!passwordEncoder.matches(
            loginRequest.getPassword(), 
            user.getPasswordHash()
        )) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        
        return buildAuthResponse(user);
    }

    @Override
    public void logout() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }

    private LoginResponseDTO buildAuthResponse(AppUser user) {

        String token = jwtProvider.generateToken(authUserDetailsMapper.toUserDetails(user));

        return LoginResponseDTO.builder()
            .token(token)
            .tokenType("Bearer")
            .expiresIn(jwtProvider.getClaims(token).getExpiration().getTime())
            .user(userMapper.toUserResponseDTO(user))
            .build();

    }
    
}
