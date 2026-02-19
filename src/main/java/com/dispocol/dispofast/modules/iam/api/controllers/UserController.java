package com.dispocol.dispofast.modules.iam.api.controllers;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("")
  public ResponseEntity<List<AppUser>> getMethodName() {
    return ResponseEntity.ok(userService.getUsers());
  }

  @PostMapping("/delete-user")
  public ResponseEntity<Void> deleteUser(@RequestParam String email) {

    userService.deleteUser(email);

    return ResponseEntity.noContent().build();
  }
}
