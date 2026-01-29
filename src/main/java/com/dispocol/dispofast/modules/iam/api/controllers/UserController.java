package com.dispocol.dispofast.modules.iam.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserRequestDTO userRequest) {
        UserResponseDTO entity = userService.register(userRequest);
        
        return ResponseEntity.ok(entity);
    }
    
}
