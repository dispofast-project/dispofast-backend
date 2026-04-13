package com.dispocol.dispofast.modules.customers.api.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.dispocol.dispofast.shared.MediaAsset.dtos.MediaAssetDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalDocumentDTO {
  private UUID id;
  private MediaAssetDTO fileAttachment;
  private OffsetDateTime updatedAt;
  private OffsetDateTime createdAt;
}
