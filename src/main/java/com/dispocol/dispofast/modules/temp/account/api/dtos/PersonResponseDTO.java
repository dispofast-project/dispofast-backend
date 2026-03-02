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
public class PersonResponseDTO {

  private UUID id;
  private String identificationNumber;
  private String firstName;
  private String lastName;
  private String jobTitle;
  private String email;
  private String phone;
  private OrganizationResponseDTO organization;
}
