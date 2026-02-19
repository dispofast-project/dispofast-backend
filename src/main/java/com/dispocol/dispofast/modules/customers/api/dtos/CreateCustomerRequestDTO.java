package com.dispocol.dispofast.modules.customers.api.dtos;

import jakarta.validation.constraints.Email;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateCustomerRequestDTO {

  private String typePerson;
  private String socialReason;
  private String nitCedula;
  private boolean witholdingTax;

  private String address;
  private String zone;
  private String city;
  private String country;
  private String depto;
  private String phone;

  @Email private String email;

  private UUID userId;

  private CreateCustomerContactRequestDTO contact;
}
