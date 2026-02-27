package com.dispocol.dispofast.modules.temp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LegalDocumentType {
  RUT("RUT"),
  COMMERCE_CHAMBER("camara de comercio"),
  ID("cedúla"),
  OTHER("otro");

  @JsonValue private final String value;

  @JsonCreator
  public static LegalDocumentType fromValue(String value) {
    for (LegalDocumentType type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Tipo de documento legal desconocido: " + value);
  }
}
