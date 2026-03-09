package com.dispocol.dispofast.modules.orders.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateSalesOrderRequestDTO {

  @NotBlank(message = "El número de orden es obligatorio")
  @Size(max = 20, message = "El número de orden no puede superar los 20 caracteres")
  private String orderNumber;

  @NotNull(message = "La cuenta es obligatoria")
  private UUID accountId;

  @NotNull(message = "El asesor es obligatorio")
  private UUID asesorUserId;

  private OffsetDateTime orderDate;

  @NotBlank(message = "La ciudad de despacho es obligatoria")
  @Size(max = 10, message = "El código de ciudad no puede superar los 10 caracteres")
  private String shipmentCityId;

  @NotBlank(message = "La dirección de despacho es obligatoria")
  private String shipmentAddress;

  @Size(max = 50, message = "La zona no puede superar los 50 caracteres")
  private String zone;

  @NotNull(message = "La lista de precios es obligatoria")
  private UUID accountPriceListId;

  @NotNull(message = "El usuario creador es obligatorio")
  private UUID userId;

  private UUID quoteId;

  @NotEmpty(message = "La orden debe contener al menos un ítem")
  @Valid
  private List<CreateSalesOrderItemDTO> items;
}
