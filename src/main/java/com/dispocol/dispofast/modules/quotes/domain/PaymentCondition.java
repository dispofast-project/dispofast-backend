package com.dispocol.dispofast.modules.quotes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentCondition {
  CONTADO("Contado"),
  CREDITO_15_DIAS("Crédito 15 días"),
  CREDITO_30_DIAS("Crédito 30 días"),
  CREDITO_60_DIAS("Crédito 60 días"),
  CREDITO_90_DIAS("Crédito 90 días"),
  CONTRAENTREGA("Contraentrega");

  private final String value;
}
