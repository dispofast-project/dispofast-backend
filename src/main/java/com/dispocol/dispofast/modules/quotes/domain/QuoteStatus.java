package com.dispocol.dispofast.modules.quotes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuoteStatus {
  DRAFT("borrador"),
  SENT("enviada"),
  ACCEPTED("aceptada"),
  REJECTED("rechazada"),
  EXPIRED("caducada");

  private final String value;
}
