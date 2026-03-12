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
public class CreateIndividualRequestDTO extends CreateClientRequestDTO {

  @NotBlank(message = "El nombre es requerido")
  private String firstName;

  @NotBlank(message = "El apellido es requerido")
  private String lastName;

  private String representativeFirstName;
  private String representativeLastName;
  private String representativeIdentification;
  private String representativeJobTitle;

  @Email(message = "El correo del representante no es válido")
  private String representativeEmail;

  private String representativePhone;
}
