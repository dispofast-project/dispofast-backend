package com.dispocol.dispofast.modules.temp.account.api.mappers;

import com.dispocol.dispofast.modules.temp.account.Organization;
import com.dispocol.dispofast.modules.temp.account.api.dtos.OrganizationResponseDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

  OrganizationResponseDTO toResponseDTO(Organization organization);

  List<OrganizationResponseDTO> toResponseDTOList(List<Organization> organizations);
}
