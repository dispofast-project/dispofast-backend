package com.dispocol.dispofast.modules.quotes.api.mappers;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.shared.location.api.dto.LocationDTO;
import com.dispocol.dispofast.shared.location.domain.Location;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(target = "status", source = "status")
    @Mapping(target = "account.id", source = "accountId")
    @Mapping(target = "seller.id", source = "sellerId")
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "priceList.id", source = "priceListId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Quotes toEntity(CreateQuoteRequestDTO createQuoteRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "seller.id", source = "sellerId")
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "priceList.id", source = "priceListId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(UpdateQuoteRequestDTO updateQuoteRequestDTO, @MappingTarget Quotes quotes);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "priceListId", source = "priceList.id")
    @Mapping(target = "sellerName", expression = "java(quotes.getSeller() != null ? quotes.getSeller().getFullName() : null)")
    @Mapping(target = "location", source = "location")
    QuoteResponseDTO toResponseDTO(Quotes quotes);

    List<QuoteResponseDTO> toResponseDTOList(List<Quotes> quotesList);

    LocationDTO toLocationDTO(Location location);
}
