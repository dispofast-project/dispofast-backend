package com.dispocol.dispofast.modules.orders.api.dtos;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import java.util.UUID;
import lombok.Data;

@Data
public class SalesOrderFilterDTO {

  private OrderState state;
  private UUID clientId;
  private UUID asesorUserId;
  private String orderNumber;
}
