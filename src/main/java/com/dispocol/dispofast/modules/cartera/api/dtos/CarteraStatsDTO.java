package com.dispocol.dispofast.modules.cartera.api.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarteraStatsDTO {

  private BigDecimal totalCartera;
  private BigDecimal carteraVencida;
}
