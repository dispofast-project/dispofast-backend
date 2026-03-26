package com.dispocol.dispofast.modules.quotes.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentCondition {
  CONTADO("Contado"),
  CONTADO_15_DIAS("Contado 15 días"),
  CONTADO_30_DIAS("Contado 30 días"),
  CONTADO_60_DIAS("Contado 60 días"),
  CONTADO_90_DIAS("Contado 90 días"),
  CONTRAENTREGA("Contraentrega");

  @JsonValue private final String value;

  @JsonCreator
  public static PaymentCondition fromValue(String value) {
    for (PaymentCondition condition : values()) {
      if (condition.value.equalsIgnoreCase(value) || condition.name().equalsIgnoreCase(value)) {
        return condition;
      }
    }
    throw new IllegalArgumentException("Condición de pago desconocida: " + value);
  }
}
