package com.dispocol.dispofast.shared.MediaAsset.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MediaAssetType {
  INVOICE("factura"),
  LEGAL_DOC("documento legal");

  @JsonValue private final String value;

  @JsonCreator
  public static MediaAssetType fromValue(String value) {
    for (MediaAssetType type : values()) {
      if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Tipo de activo desconocido: " + value);
  }
}
