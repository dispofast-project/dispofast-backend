package com.dispocol.dispofast.modules.quotes.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OfferValidity {
  DIAS_15("15 días"),
  DIAS_30("30 días"),
  DIAS_45("45 días"),
  DIAS_60("60 días");

  @JsonValue private final String value;

  @JsonCreator
  public static OfferValidity fromValue(String value) {
    for (OfferValidity validity : values()) {
      if (validity.value.equalsIgnoreCase(value) || validity.name().equalsIgnoreCase(value)) {
        return validity;
      }
    }
    throw new IllegalArgumentException("Validez de oferta desconocida: " + value);
  }
}
