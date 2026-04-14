package com.dispocol.dispofast.modules.temp.api.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAssetDTO {
  private UUID id;
  private String filename;
  private String storagePath;
  private String mimeType;
  private Long fileSize;
  private String type;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
