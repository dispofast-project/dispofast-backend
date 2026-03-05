package com.dispocol.dispofast.modules.customers.api.dtos;

import com.dispocol.dispofast.modules.customers.domain.LegalEntityType;
import com.dispocol.dispofast.modules.iam.api.dtos.UserPreview;
import com.dispocol.dispofast.shared.location.api.dto.LocationDTO;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPreviewDTO {
  private UUID id;
  private LegalEntityType legalEntityType;
  private String name;
  private String identificationNumber;
  private Boolean isActive;
  private UserPreview defaultAdvisor;
  private LocationDTO location;
}
