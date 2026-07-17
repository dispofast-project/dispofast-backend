package com.dispocol.dispofast.modules.customers.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RetefuenteType {
  PERSONA_JURIDICA("persona_juridica"),
  PERSONA_NATURAL("persona_natural"),
  NO_APLICA("no_aplica");

  @JsonValue private final String value;

  @JsonCreator
  public static RetefuenteType fromValue(String value) {
    if (value == null) {
      return null;
    }

    for (RetefuenteType type : values()) {
      if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }

    throw new IllegalArgumentException("Tipo de retención en la fuente no válido: " + value);
  }
}
