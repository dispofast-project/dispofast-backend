package com.dispocol.dispofast.modules.iam.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.domain.AppUser;

import jakarta.inject.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserResponseDTO toUserResponseDTO(AppUser user);

    @Named("mapRoles")
    default String mapRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return "";
        }
        return String.join(",", roles);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(
        target = "email", 
        expression = "java(userRequest != null ? userRequest.getEmail().trim().toLowerCase() : null)"
    )
    @Mapping(
        target = "fullName", 
        expression = "java(userRequest != null ? userRequest.getFullName().trim() : null)"
    )
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AppUser fromCreateUserRequestDTO(CreateUserRequestDTO userRequest);
}
