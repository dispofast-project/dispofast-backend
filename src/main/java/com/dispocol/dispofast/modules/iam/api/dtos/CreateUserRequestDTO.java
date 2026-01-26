package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.Set;

import com.dispocol.dispofast.modules.iam.domain.Role;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CreateUserRequestDTO {
    
    private String name;

    @Email
    private String email;

    private String password;

    private Set<Role> roles;

}
