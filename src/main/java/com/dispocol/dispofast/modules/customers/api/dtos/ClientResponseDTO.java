package com.dispocol.dispofast.modules.customers.api.dtos;

import com.dispocol.dispofast.modules.customers.domain.LegalEntityType;
import com.dispocol.dispofast.modules.iam.api.dtos.UserPreview;
import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListResponseDTO;
import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class ClientResponseDTO {
  private UUID id;
  private LegalEntityType legalEntityType;
  private String name;
  private String identificationNumber;
  private String email;
  private String phone;
  private Boolean isActive;
  private Boolean retefuenteApplies;
  private String address;
  private UserPreview defaultAdvisor;
  private CityDTO city;
  private LocationZone zone;
  private Integer defaultDiscountRate;
  private PriceListResponseDTO priceList;
  private ClientTypeDTO clientType;
  private List<LegalDocumentDTO> legalDocuments;
}
