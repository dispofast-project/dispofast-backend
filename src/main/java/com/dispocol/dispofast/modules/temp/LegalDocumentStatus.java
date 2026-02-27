package com.dispocol.dispofast.modules.temp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LegalDocumentStatus {
  ACTIVE("activo"),
  EXPIRED("expirado");

  @JsonValue private final String value;

  @JsonCreator
  public static LegalDocumentStatus fromValue(String value) {
    for (LegalDocumentStatus status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Estado de documento legal desconocido: " + value);
  }
}
