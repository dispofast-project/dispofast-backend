package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private List<String> roles;
}