package com.dispocol.dispofast.modules.iam.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private String token;
    private String tokenType;
    private long expiresIn;

    private UserResponseDTO user;
}
