package com.dispocol.dispofast.modules.iam.api.controllers;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserCommissionRateDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserCommissionRateDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserCommissionRateService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/commission-rates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserCommissionRateController {

  private final UserCommissionRateService commissionRateService;

  @GetMapping
  public ResponseEntity<List<UserCommissionRateDTO>> getCommissionRates(@PathVariable UUID userId) {
    return ResponseEntity.ok(commissionRateService.getCommissionRates(userId));
  }

  @PostMapping
  public ResponseEntity<UserCommissionRateDTO> createCommissionRate(
      @PathVariable UUID userId, @Valid @RequestBody CreateUserCommissionRateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(commissionRateService.createCommissionRate(userId, dto));
  }

  @DeleteMapping("/{rateId}")
  public ResponseEntity<Void> deleteCommissionRate(
      @PathVariable UUID userId, @PathVariable UUID rateId) {
    commissionRateService.deleteCommissionRate(userId, rateId);
    return ResponseEntity.noContent().build();
  }
}
