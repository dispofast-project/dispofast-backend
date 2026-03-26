package com.dispocol.dispofast.modules.quotes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OfferValidity {
  DIAS_15("15 días"),
  DIAS_30("30 días"),
  DIAS_45("45 días"),
  DIAS_60("60 días");

  private final String value;
}
