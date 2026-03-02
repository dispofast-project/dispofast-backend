package com.dispocol.dispofast.modules.temp.account.api.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationResponseDTO {

  private UUID id;
  private String nit;
  private String legalName;
  private Integer defaultDiscountRate;
  private String address;
  private String billingEmail;
  private String generalEmail;
  private String phone;
}
