package com.dispocol.dispofast.modules.iam.api.controllers;

import com.dispocol.dispofast.modules.iam.api.dtos.RoleResponseDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.RoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

  private final RoleService roleService;

  @GetMapping
  public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
    return ResponseEntity.ok(roleService.getAllRoles());
  }
}
