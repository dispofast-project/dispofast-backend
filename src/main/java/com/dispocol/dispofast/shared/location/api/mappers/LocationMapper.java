package com.dispocol.dispofast.shared.location.api.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.dispocol.dispofast.shared.location.api.dto.LocationDTO;
import com.dispocol.dispofast.shared.location.domain.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDTO toLocationDTO(Location location);

    List<LocationDTO> toLocationDTOList(List<Location> locations);

}
