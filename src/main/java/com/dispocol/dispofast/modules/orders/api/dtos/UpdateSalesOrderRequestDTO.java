package com.dispocol.dispofast.modules.orders.api.dtos;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateSalesOrderRequestDTO {

  private UUID asesorUserId;

  private OrderState state;

  private OffsetDateTime orderDate;

  @Size(max = 10, message = "El código de ciudad no puede superar los 10 caracteres")
  private String shipmentCityId;

  private String shipmentAddress;

  @Size(max = 50, message = "La zona no puede superar los 50 caracteres")
  private String zone;

  private UUID accountPriceListId;

  @Valid
  private List<CreateSalesOrderItemDTO> items;
}
