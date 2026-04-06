package com.dispocol.dispofast.modules.orders.api.dtos;

import com.dispocol.dispofast.modules.orders.domain.PaymentCondition;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateSalesOrderRequestDTO {

  @NotBlank(message = "El número de orden es obligatorio")
  @Size(max = 20, message = "El número de orden no puede superar los 20 caracteres")
  private String orderNumber;

  @NotNull(message = "El cliente es obligatorio")
  private UUID clientId;

  @NotNull(message = "El asesor es obligatorio")
  private UUID asesorUserId;

  private OffsetDateTime orderDate;

  @NotBlank(message = "La ciudad de despacho es obligatoria")
  @Size(max = 10, message = "El código de ciudad no puede superar los 10 caracteres")
  private String shipmentCityId;

  @NotBlank(message = "La dirección de despacho es obligatoria")
  private String shipmentAddress;

  private LocationZone zone;

  @NotNull(message = "La lista de precios es obligatoria")
  private UUID priceListId;

  private UUID quoteId;

  private PaymentCondition paymentCondition;

  private Integer discountRate;

  private BigDecimal additionalDiscountRate;

  private BigDecimal retefuenteAmount;

  private BigDecimal reteicaAmount;

  private BigDecimal freight;

  @NotEmpty(message = "La orden debe contener al menos un ítem")
  @Valid
  private List<CreateSalesOrderItemDTO> items;
}
