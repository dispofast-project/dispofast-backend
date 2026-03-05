package com.dispocol.dispofast.modules.iam.application.impl;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.PermissionOverrideDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserPermissionRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserPermissionsDetailDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapper;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.domain.Permission;
import com.dispocol.dispofast.modules.iam.domain.Role;
import com.dispocol.dispofast.modules.iam.domain.UserPermission;
import com.dispocol.dispofast.modules.iam.domain.UserPermissionId;
import com.dispocol.dispofast.modules.iam.infra.exceptions.PermissionNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.exceptions.RoleNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserAlreadyExistsException;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.persistence.PermissionRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.RoleRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserResponseDTO register(CreateUserRequestDTO userRequest) {

    String email;

    email = userRequest.getEmail().trim().toLowerCase();

    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new UserAlreadyExistsException("El usuario ya existe con el correo: " + email);
    }

    Role role =
        roleRepository
            .findById(userRequest.getRoleId())
            .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

    AppUser newUser = userMapper.fromCreateUserRequestDTO(userRequest);
    newUser.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
    newUser.setActive(true);
    newUser.getRoles().add(role);

    newUser = userRepository.save(newUser);

    return userMapper.toUserResponseDTO(newUser);
  }

  @Override
  public UserResponseDTO updatedUser(String id, UpdateUserRequestDTO user) {
    AppUser existingUser = getUserById(UUID.fromString(id));

    Role role =
        roleRepository
            .findById(user.getRoleId())
            .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

    existingUser.setFullName(user.getName());
    existingUser.setEmail(user.getEmail().trim().toLowerCase());
    existingUser.getRoles().clear();
    existingUser.getRoles().add(role);

    existingUser = userRepository.save(existingUser);

    return userMapper.toUserResponseDTO(existingUser);
  }

  @Override
  public UserPermissionsDetailDTO updateUserPermissions(
      UUID id, UpdateUserPermissionRequestDTO request) {
    AppUser user = getUserById(id);

    Set<UUID> permissionsIds =
        request.getPermissions().stream()
            .map(PermissionOverrideDTO::getPermissionId)
            .collect(Collectors.toSet());

    List<Permission> permissions = permissionRepository.findAllById(permissionsIds);

    if (permissions.size() != permissionsIds.size()) {
      throw new PermissionNotFoundException("Alguna de las permisoso no fue encontrada");
    }

    user.getPermissions().clear();

    request
        .getPermissions()
        .forEach(
            overrideDto -> {
              Permission permission =
                  permissions.stream()
                      .filter(p -> p.getId().equals(overrideDto.getPermissionId()))
                      .findFirst()
                      .get();

              UserPermission override = new UserPermission();
              override.setId(new UserPermissionId(user.getId(), permission.getId()));
              override.setUser(user);
              override.setPermission(permission);
              override.setGranted(overrideDto.getGranted());

              user.getPermissions().add(override);
            });

    userRepository.save(user);

    return userMapper.toUserPermissionsDetailDTO(user);
  }

  @Override
  public Page<AppUser> searchUsers(String search, Pageable pageable) {
    return userRepository.findByFullNameContainingIgnoreCase(search, pageable);
  }

  @Override
  public void deleteUser(String email) {
    AppUser user = getUserByEmail(email);

    if (user == null) {
      throw new UserNotFoundException("No se encontró un usuario con el correo: " + email);
    }
    userRepository.delete(user);
  }

  @Override
  public List<AppUser> getUsers() {
    return userRepository.findAll();
  }

  @Override
  public AppUser getUserByEmail(String email) {
    return userRepository
        .findByEmailIgnoreCase(email)
        .orElseThrow(
            () -> new UserNotFoundException("No se encontró un usuario con el correo: " + email));
  }

  @Override
  public Page<AppUser> getUsersPaged(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Override
  public UserPermissionsDetailDTO getUserPermissions(UUID id) {
    return userMapper.toUserPermissionsDetailDTO(getUserById(id));
  }

  private AppUser getUserById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("No se encontró un usuario con el id: " + id));
  }
}
