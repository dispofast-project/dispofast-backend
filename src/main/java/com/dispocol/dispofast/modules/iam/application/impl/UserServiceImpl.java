package com.dispocol.dispofast.modules.iam.application.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapper;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.iam.infra.security.PasswordConfig;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO register(CreateUserRequestDTO userRequest) {

        AppUser newUser;
        String email;

        newUser = new AppUser();
        email = userRequest.getEmail();

        if (userRepository.existsByEmailIgnoreCase(email.trim())) {
            throw new IllegalStateException("El usuario ya existe con el correo: " 
                + email
            );
        }

        newUser = userMapper.fromCreateUserRequestDTO(userRequest);
        newUser.setPasswordHash(passwordConfig.passwordEncoder().encode(userRequest.getPassword()));
        newUser.setActive(true);

        newUser = userRepository.save(newUser);

        return userMapper.toUserResponseDTO(newUser);
    }

    @Override
    public Page<AppUser> searchUsers(String search, Pageable pageable) {
        return userRepository.findByFullNameContainingIgnoreCase(search, pageable);
    }

    @Override
    public void deleteUser(AppUser user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public List<AppUser> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalStateException(
                "No se encontró un usuario con el correo: " + email
            ));
    }

    @Override
    public Page<AppUser> getUsersPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
}
