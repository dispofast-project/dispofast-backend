package com.dispocol.dispofast.modules.iam.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

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
    
    @DeleteMapping("/delete-user")
    public ResponseEntity<Void> deleteUser(@RequestParam String email) {
        
        userService.deleteUser(email);
        
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("")
    public ResponseEntity<Page<UserResponseDTO>> getUsersPaged(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPaged(pageable));
    }

    @GetMapping("/by-email")
    public ResponseEntity<AppUser> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
    
    @GetMapping("/search-users")
    public ResponseEntity<Page<AppUser>> searchUsers(@RequestParam String search, Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(search, pageable));
    }
    
    
}
