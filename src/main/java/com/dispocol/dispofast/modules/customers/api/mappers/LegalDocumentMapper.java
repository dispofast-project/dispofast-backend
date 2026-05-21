package com.dispocol.dispofast.modules.customers.api.mappers;

import com.dispocol.dispofast.modules.customers.api.dtos.LegalDocumentDTO;
import com.dispocol.dispofast.modules.customers.domain.LegalDocument;
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAsset;
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAssetType;
import com.dispocol.dispofast.shared.MediaAsset.dtos.MediaAssetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LegalDocumentMapper {

  LegalDocumentDTO toDTO(LegalDocument legalDocument);

  @Mapping(target = "type", expression = "java(mapType(asset.getType()))")
  MediaAssetDTO toMediaAssetDTO(MediaAsset asset);

  default String mapType(MediaAssetType type) {
    return type != null ? type.name() : null;
  }
}
