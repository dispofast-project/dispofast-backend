package com.dispocol.dispofast.modules.customers.api.mappers;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateIndividualRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateOrganizationRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.IndividualResponseDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.OrganizationResponseDTO;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapper;
import com.dispocol.dispofast.shared.location.api.mappers.CityMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, CityMapper.class})
public interface ClientMapper {

  @Mapping(target = "name", ignore = true)
  ClientPreviewDTO toPreviewDTO(Client client);

  default ClientResponseDTO toResponseDTO(Client client) {
    if (client instanceof Individual individual) {
      return toIndividualResponseDTO(individual);
    } else if (client instanceof Organization organization) {
      return toOrganizationResponseDTO(organization);
    }
    return null;
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "defaultAdvisor", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "clientType", ignore = true)
  @Mapping(target = "legalDocuments", ignore = true)
  Individual toIndividual(CreateIndividualRequestDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "defaultAdvisor", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "clientType", ignore = true)
  @Mapping(target = "legalDocuments", ignore = true)
  Organization toOrganization(CreateOrganizationRequestDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "legalEntityType", ignore = true)
  @Mapping(target = "defaultAdvisor", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "clientType", ignore = true)
  @Mapping(target = "legalDocuments", ignore = true)
  void updateIndividual(CreateIndividualRequestDTO dto, @MappingTarget Individual entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "legalEntityType", ignore = true)
  @Mapping(target = "defaultAdvisor", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "clientType", ignore = true)
  @Mapping(target = "legalDocuments", ignore = true)
  void updateOrganization(CreateOrganizationRequestDTO dto, @MappingTarget Organization entity);

  @Mapping(target = "name", ignore = true)
  IndividualResponseDTO toIndividualResponseDTO(Individual individual);

  @Mapping(target = "name", ignore = true)
  OrganizationResponseDTO toOrganizationResponseDTO(Organization organization);

  @AfterMapping
  default void mapNamePreview(Client client, @MappingTarget ClientPreviewDTO dto) {
    if (client instanceof Individual individual) {
      String firstName = individual.getFirstName() != null ? individual.getFirstName() : "";
      String lastName = individual.getLastName() != null ? " " + individual.getLastName() : "";
      dto.setName((firstName + lastName).trim());
    } else if (client instanceof Organization organization) {
      dto.setName(organization.getLegalName());
    }
  }

  @AfterMapping
  default void mapNameIndividualResponse(
      Individual individual, @MappingTarget IndividualResponseDTO dto) {
    String firstName = individual.getFirstName() != null ? individual.getFirstName() : "";
    String lastName = individual.getLastName() != null ? " " + individual.getLastName() : "";
    dto.setName((firstName + lastName).trim());
  }

  @AfterMapping
  default void mapNameOrganizationResponse(
      Organization organization, @MappingTarget OrganizationResponseDTO dto) {
    dto.setName(organization.getLegalName());
  }
}
