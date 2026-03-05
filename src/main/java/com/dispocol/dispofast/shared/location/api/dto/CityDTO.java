package com.dispocol.dispofast.shared.location.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO {
  private String code;
  private String name;
  private DepartmentDTO department;
}
