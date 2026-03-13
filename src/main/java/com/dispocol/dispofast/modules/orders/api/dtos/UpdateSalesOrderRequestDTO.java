package com.dispocol.dispofast.modules.orders.api.dtos;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
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

  private LocationZone zone;

  private UUID priceListId;

  @Valid private List<CreateSalesOrderItemDTO> items;
}
