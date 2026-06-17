package com.dispocol.dispofast.modules.shipping.api.dtos;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverResponseDTO {

  private UUID id;
  private String name;
  private String phone;
  private String cedula;
  private LocalDate createdAt;
}
