package com.dispocol.dispofast.modules.iam.api.mappers;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.domain.Permission;
import com.dispocol.dispofast.modules.iam.domain.Role;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "name", source = "fullName")
  @Mapping(target = "permissions", source = "permissions", qualifiedByName = "mapPermissions")
  @Mapping(target = "role", source = "roles", qualifiedByName = "mapRole")
  UserResponseDTO toUserResponseDTO(AppUser user);

  @Named("mapPermissions")
  default List<String> mapPermissions(Set<Permission> permissions) {
    if (permissions == null || permissions.isEmpty()) {
      return java.util.Collections.emptyList();
    }
    return permissions.stream().map(Permission::getName).collect(Collectors.toList());
  }

  @Named("mapRole")
  default String mapRole(Set<Role> roles) {
    if (roles == null || roles.isEmpty()) return null;
    return roles.iterator().next().getName();
  }

  @Named("formatDateTime")
  default String formatDateTime(OffsetDateTime dateTime) {
    return dateTime != null ? dateTime.toString() : null;
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "permissions", source = "permissions")
  @Mapping(target = "roles", source = "roles")
  @Mapping(
      target = "email",
      expression = "java(userRequest != null ? userRequest.getEmail().trim().toLowerCase() : null)")
  @Mapping(
      target = "fullName",
      expression = "java(userRequest != null ? userRequest.getName().trim() : null)")
  @Mapping(
      target = "passwordHash",
      expression =
          "java(userRequest != null && userRequest.getPassword() != null ? userRequest.getPassword().trim() : null)")
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "customers", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  AppUser fromCreateUserRequestDTO(CreateUserRequestDTO userRequest);
}
