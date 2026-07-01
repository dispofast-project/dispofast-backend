package com.dispocol.dispofast.modules.shipping.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShipmentState {
  PENDING("pendiente"),
  ASSIGNED("asignado"),
  IN_ROUTE("en ruta"),
  DELIVERED("entregado"),
  DELAYED("retrasado");

  private final String value;
}
