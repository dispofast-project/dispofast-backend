package com.dispocol.dispofast.modules.quotes.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProspectDTO {
  private String name;
  private String legalEntityType;
  private Long clientTypeId;
  private String clientTypeName;
  private String phone;
  private String email;
}
