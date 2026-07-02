package com.dispocol.dispofast.modules.orders.domain;

import java.util.EnumSet;
import java.util.Set;
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

  public static final Set<OrderState> TERMINAL_STATES = EnumSet.of(DELIVERED, CANCELLED);
}
