package com.dispocol.dispofast.modules.customers.api.dtos;

import com.dispocol.dispofast.modules.customers.domain.LegalEntityType;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "legalEntityType",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CreateIndividualRequestDTO.class, name = "natural"),
  @JsonSubTypes.Type(value = CreateOrganizationRequestDTO.class, name = "empresa")
})
public abstract class CreateClientRequestDTO {

  @NotNull(message = "El tipo de entidad legal es requerido")
  private LegalEntityType legalEntityType;

  @NotBlank(message = "El número de identificación es requerido")
  private String identificationNumber;

  @NotBlank(message = "El correo electrónico es requerido")
  @Email(message = "El correo electrónico no es válido")
  private String email;

  @NotBlank(message = "El teléfono es requerido")
  private String phone;

  @Builder.Default private Boolean isActive = true;

  @Builder.Default private Boolean retefuenteApplies = false;

  @NotBlank(message = "La dirección es requerida")
  private String address;

  @NotNull(message = "El asesor asignado es requerido")
  private UUID defaultAdvisorId;

  @NotBlank(message = "La ciudad es requerida")
  private String cityCode;

  @NotNull(message = "La zona es requerida")
  private LocationZone zone;

  @NotNull(message = "El descuento por defecto es requerido")
  @Min(value = 0, message = "El descuento no puede ser menor a 0")
  @Max(value = 100, message = "El descuento no puede ser mayor a 100")
  private Integer defaultDiscountRate;

  @NotNull(message = "La lista de precios es requerida")
  private UUID priceListId;

  @NotNull(message = "El tipo de cliente es requerido")
  private Long clientTypeId;
}
