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
  CENTRO("centro");

  private final String value;

  @JsonValue
  public String getFormattedValue() {
    return "zona " + value;
  }

  @JsonCreator
  public static LocationZone fromValue(String value) {
    String cleanValue = value.toLowerCase().replace("zona ", "").trim();

    for (LocationZone zone : values()) {
      if (zone.value.equalsIgnoreCase(cleanValue) || zone.name().equalsIgnoreCase(cleanValue)) {
        return zone;
      }
    }
    throw new IllegalArgumentException("zona no valida: " + value);
  }
}
