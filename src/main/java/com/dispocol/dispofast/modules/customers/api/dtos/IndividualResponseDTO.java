package com.dispocol.dispofast.modules.customers.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IndividualResponseDTO extends ClientResponseDTO {
  private String firstName;
  private String lastName;
  private String representativeFirstName;
  private String representativeLastName;
  private String representativeIdentification;
  private String representativeJobTitle;
  private String representativeEmail;
  private String representativePhone;
}
