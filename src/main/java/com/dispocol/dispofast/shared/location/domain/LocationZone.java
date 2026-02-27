package com.dispocol.dispofast.shared.location.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LocationZone {
  NORTE("norte"),
  SUR("sur"),
  ORIENTE("oriente"),
  OCCIDENTE("occidente"),
  CENTRO("centro"),
  METROPOLITANO("metropolitano"),
  OTRO("otro");

  @JsonValue private final String value;

  @JsonCreator
  public static LocationZone fromValue(String value) {
    for (LocationZone zone : values()) {
      if (zone.value.equalsIgnoreCase(value) || zone.name().equalsIgnoreCase(value)) {
        return zone;
      }
    }
    throw new IllegalArgumentException("Zona no valida: " + value);
  }
}
