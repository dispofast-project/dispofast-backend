package com.dispocol.dispofast.modules.customers.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateOrganizationRequestDTO extends CreateClientRequestDTO {

  @NotBlank(message = "La razón social es requerida")
  private String legalName;

  @Email(message = "El correo de facturación no es válido")
  private String billingEmail;

  @NotBlank(message = "El nombre del representante es requerido")
  private String representativeFirstName;

  @NotBlank(message = "El apellido del representante es requerido")
  private String representativeLastName;

  @NotBlank(message = "La identificación del representante es requerida")
  private String representativeIdentification;

  @NotBlank(message = "El correo del representante es requerido")
  @Email(message = "El correo del representante no es válido")
  private String representativeEmail;

  @NotBlank(message = "El teléfono del representante es requerido")
  private String representativePhone;
}
