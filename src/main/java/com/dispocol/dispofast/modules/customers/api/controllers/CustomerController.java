package com.dispocol.dispofast.modules.customers.api.controllers;

import com.dispocol.dispofast.modules.customers.api.dtos.CreateCustomerRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CustomerResponseDTO;
import com.dispocol.dispofast.modules.customers.application.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping("/create-customer")
  public ResponseEntity<CustomerResponseDTO> createCustomer(
      @RequestBody CreateCustomerRequestDTO entity) {
    return ResponseEntity.ok(customerService.createCustomer(entity));
  }

  @GetMapping()
  public ResponseEntity<Page<CustomerResponseDTO>> getCustomersPaged(
      @RequestParam int page, @RequestParam int size) {
    return ResponseEntity.ok(customerService.getAllCustomers(page, size));
  }
}
