package com.dispocol.dispofast.modules.quotes.api.mappers;

import com.dispocol.dispofast.modules.customers.api.mappers.ClientMapper;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.ProspectDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuotePreviewResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.domain.Prospect;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.shared.location.api.mappers.CityMapper;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = {ClientMapper.class, CityMapper.class, QuoteItemMapper.class},
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface QuoteMapper {

  @Mapping(target = "account", ignore = true)
  @Mapping(target = "prospect", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "seller", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "zone", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Quotes toEntity(CreateQuoteRequestDTO createQuoteRequestDTO);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "status", source = "status")
  @Mapping(target = "seller", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "zone", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "number", ignore = true)
  @Mapping(target = "account", ignore = true)
  @Mapping(target = "prospect", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  // Campos calculados — ignorados en el update, el servicio los recalcula
  @Mapping(target = "subtotalAmount", ignore = true)
  @Mapping(target = "commercialDiscountAmount", ignore = true)
  @Mapping(target = "otherDiscountsAmount", ignore = true)
  @Mapping(target = "ivaRate", ignore = true)
  @Mapping(target = "ivaAmount", ignore = true)
  @Mapping(target = "retefuenteRate", ignore = true)
  @Mapping(target = "retefuenteAmount", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "items", ignore = true)
  void updateEntityFromDTO(
      UpdateQuoteRequestDTO updateQuoteRequestDTO, @MappingTarget Quotes quotes);

  @Mapping(target = "status", source = "status")
  @Mapping(target = "account", source = "account")
  @Mapping(target = "prospect", source = "prospect", qualifiedByName = "prospectToDTO")
  @Mapping(target = "location", source = "city")
  @Mapping(target = "priceList", source = "priceList")
  @Mapping(target = "items", source = "items")
  @Mapping(
      target = "sellerId",
      expression = "java(quotes.getSeller() != null ? quotes.getSeller().getId() : null)")
  @Mapping(
      target = "sellerName",
      expression = "java(quotes.getSeller() != null ? quotes.getSeller().getFullName() : null)")
  QuoteResponseDTO toResponseDTO(Quotes quotes);

  @Mapping(
      target = "accountName",
      expression = "java(resolveAccountName(quote))")
  @Mapping(
      target = "prospect",
      expression = "java(quote.getAccount() == null && quote.getProspect() != null)")
  @Mapping(target = "total", source = "totalAmount")
  QuotePreviewResponseDTO toPreviewResponseDTO(Quotes quote);

  default String resolveAccountName(Quotes quote) {
    if (quote.getAccount() != null) {
      return clientToName(quote.getAccount());
    }
    if (quote.getProspect() != null) {
      return quote.getProspect().getName();
    }
    return null;
  }

  @Named("clientToName")
  default String clientToName(Client client) {
    if (client == null) return null;
    if (client instanceof Individual ind) {
      String firstName = ind.getFirstName() != null ? ind.getFirstName() : "";
      String lastName = ind.getLastName() != null ? " " + ind.getLastName() : "";
      return (firstName + lastName).trim();
    }
    if (client instanceof Organization org) {
      return org.getLegalName();
    }
    return "";
  }

  @Named("prospectToDTO")
  default ProspectDTO prospectToDTO(Prospect prospect) {
    if (prospect == null) return null;
    return ProspectDTO.builder()
        .name(prospect.getName())
        .legalEntityType(prospect.getLegalEntityType() != null ? prospect.getLegalEntityType().getValue() : null)
        .clientTypeId(prospect.getClientType() != null ? prospect.getClientType().getId() : null)
        .clientTypeName(prospect.getClientType() != null ? prospect.getClientType().getName() : null)
        .phone(prospect.getPhone())
        .email(prospect.getEmail())
        .build();
  }

  List<QuoteResponseDTO> toResponseDTOList(List<Quotes> quotesList);
}
