package com.dispocol.dispofast.modules.iam.api.controllers;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserProductAllocationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/inventory-allocations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserProductAllocationController {

  private final UserProductAllocationService allocationService;

  @GetMapping
  public ResponseEntity<List<UserProductAllocationDTO>> getAllocations(@PathVariable UUID userId) {
    return ResponseEntity.ok(allocationService.getAllocations(userId));
  }

  @PostMapping
  public ResponseEntity<UserProductAllocationDTO> createAllocation(
      @PathVariable UUID userId, @Valid @RequestBody CreateUserProductAllocationDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(allocationService.createAllocation(userId, dto));
  }

  @PutMapping("/{allocationId}")
  public ResponseEntity<UserProductAllocationDTO> updateAllocation(
      @PathVariable UUID userId,
      @PathVariable UUID allocationId,
      @Valid @RequestBody UpdateUserProductAllocationDTO dto) {
    return ResponseEntity.ok(allocationService.updateAllocation(userId, allocationId, dto));
  }

  @DeleteMapping("/{allocationId}")
  public ResponseEntity<Void> deleteAllocation(
      @PathVariable UUID userId, @PathVariable UUID allocationId) {
    allocationService.deleteAllocation(userId, allocationId);
    return ResponseEntity.noContent().build();
  }
}
