package com.dispocol.dispofast.modules.iam.api.controllers;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserPermissionRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserPermissionsDetailDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserResponseDTO> createUser(
      @Valid @RequestBody CreateUserRequestDTO userRequest) {
    UserResponseDTO entity = userService.register(userRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(entity);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Page<UserResponseDTO>> getUsers(
      @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
    return ResponseEntity.ok(userService.getUsersPaged(pageable));
  }

  @GetMapping("/by-email")
  public ResponseEntity<AppUser> getUserByEmail(@RequestParam String email) {
    return ResponseEntity.ok(userService.getUserByEmail(email));
  }

  @GetMapping("/search")
  public ResponseEntity<Page<UserResponseDTO>> searchUsers(
      @RequestParam String q, @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
    return ResponseEntity.ok(userService.searchUsers(q, pageable));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDTO> updateUser(
      @PathVariable UUID id, @Valid @RequestBody UpdateUserRequestDTO dto) {
    return ResponseEntity.ok(userService.updatedUser(id.toString(), dto));
  }

  @GetMapping("/{id}/permissions")
  public ResponseEntity<UserPermissionsDetailDTO> getUserPermissions(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserPermissions(id));
  }

  @PatchMapping("/{id}/permissions")
  public ResponseEntity<UserPermissionsDetailDTO> updateUserPermissions(
      @PathVariable UUID id, @Valid @RequestBody UpdateUserPermissionRequestDTO dto) {
    return ResponseEntity.ok(userService.updateUserPermissions(id, dto));
  }
}
