package com.dispocol.dispofast.modules.customers.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganizationResponseDTO extends ClientResponseDTO {
  private String legalName;
  private String billingEmail;
  private String representativeFirstName;
  private String representativeLastName;
  private String representativeIdentification;
  private String representativeEmail;
  private String representativePhone;
}
