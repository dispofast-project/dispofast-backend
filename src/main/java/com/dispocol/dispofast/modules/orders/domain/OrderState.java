package com.dispocol.dispofast.modules.orders.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderState {
  PENDING("pendiente"),
  INVOICED("facturado"),
  ASSIGNED("asignado"),
  IN_TRANSIT("en ruta"),
  DELIVERED("entregado"),
  CANCELLED("cancelado");

  private final String value;
}
