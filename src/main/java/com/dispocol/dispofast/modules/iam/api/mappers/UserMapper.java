package com.dispocol.dispofast.modules.iam.api.mappers;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.PermissionOverrideDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserPermissionsDetailDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.domain.Role;
import com.dispocol.dispofast.modules.iam.domain.UserPermission;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", expression = "java(user.getId().toString())")
  @Mapping(target = "name", source = "fullName")
  @Mapping(target = "email", source = "email")
  @Mapping(target = "role", source = "roles", qualifiedByName = "mapRole")
  @Mapping(
      target = "effectivePermissions",
      expression = "java(user.resolveEffectivePermissionNames())")
  UserResponseDTO toUserResponseDTO(AppUser user);

  @Named("mapRole")
  default String mapRole(Set<Role> roles) {
    if (roles == null || roles.isEmpty()) return null;
    return roles.iterator().next().getName();
  }

  @Named("formatDateTime")
  default String formatDateTime(OffsetDateTime dateTime) {
    return dateTime != null ? dateTime.toString() : null;
  }

  @Mapping(target = "userId", expression = "java(user.getId().toString())")
  @Mapping(target = "userName", source = "fullName")
  @Mapping(target = "role", source = "roles", qualifiedByName = "mapRole")
  @Mapping(target = "overrides", source = "permissions", qualifiedByName = "mapOverrides")
  UserPermissionsDetailDTO toUserPermissionsDetailDTO(AppUser user);

  @Named("mapOverrides")
  default Set<PermissionOverrideDTO> mapOverrides(Set<UserPermission> overrides) {
    if (overrides == null || overrides.isEmpty()) return java.util.Collections.emptySet();
    return overrides.stream()
        .map(
            o ->
                new PermissionOverrideDTO(
                    o.getPermission().getId(), o.getPermission().getName(), o.isGranted()))
        .collect(Collectors.toSet());
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fullName", expression = "java(dto.getName().trim())")
  @Mapping(target = "email", expression = "java(dto.getEmail().trim().toLowerCase())")
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "permissions", ignore = true)
  @Mapping(target = "customers", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  AppUser fromCreateUserRequestDTO(CreateUserRequestDTO dto);
}
