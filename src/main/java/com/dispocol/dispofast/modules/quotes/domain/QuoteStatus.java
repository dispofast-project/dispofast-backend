package com.dispocol.dispofast.modules.quotes.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuoteStatus {
  PENDING("pendiente"),
  ACCEPTED("aprobada"),
  REJECTED("rechazada"),
  EXPIRED("caducada");

  @JsonValue private final String value;

  @JsonCreator
  public static QuoteStatus fromValue(String value) {
    for (QuoteStatus status : values()) {
      if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Estado desconocido: " + value);
  }
}
