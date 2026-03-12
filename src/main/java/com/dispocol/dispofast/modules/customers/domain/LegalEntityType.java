package com.dispocol.dispofast.modules.customers.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LegalEntityType {
  NATURAL("natural"),
  LEGAL("empresa");

  @JsonValue private final String value;

  @JsonCreator
  public static LegalEntityType fromValue(String value) {
    if (value == null) {
      return null;
    }

    for (LegalEntityType type : values()) {
      if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }

    throw new IllegalArgumentException("Tipo de entidad legal no válido: " + value);
  }
}
