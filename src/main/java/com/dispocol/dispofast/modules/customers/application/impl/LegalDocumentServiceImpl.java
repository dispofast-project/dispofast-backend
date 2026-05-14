package com.dispocol.dispofast.modules.customers.application.impl;

import com.dispocol.dispofast.modules.customers.api.dtos.LegalDocumentDTO;
import com.dispocol.dispofast.modules.customers.api.mappers.LegalDocumentMapper;
import com.dispocol.dispofast.modules.customers.application.interfaces.LegalDocumentService;
import com.dispocol.dispofast.modules.customers.domain.LegalDocument;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.customers.infra.persistence.LegalDocumentRepository;
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAsset;
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAssetType;
import com.dispocol.dispofast.shared.MediaAsset.persistence.MediaAssetRepository;
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LegalDocumentServiceImpl implements LegalDocumentService {

  private static final String BUCKET = "dispofast-legal-docs";

  private final LegalDocumentRepository legalDocumentRepository;
  private final MediaAssetRepository mediaAssetRepository;
  private final ClientRepository clientRepository;
  private final LegalDocumentMapper legalDocumentMapper;
  private final S3Service s3Service;

  @Override
  @Transactional
  public LegalDocumentDTO upload(UUID clientId, MultipartFile file) {
    var client =
        clientRepository
            .findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clientId));

    String storageKey = clientId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
    try {
      s3Service.uploadFile(
          BUCKET, storageKey, file.getInputStream(), file.getContentType(), file.getSize());
    } catch (IOException e) {
      throw new IllegalArgumentException("Error al subir el archivo: " + e.getMessage(), e);
    }

    MediaAsset asset = new MediaAsset();
    asset.setFilename(file.getOriginalFilename());
    asset.setStoragePath(storageKey);
    asset.setMimeType(file.getContentType());
    asset.setFileSize(file.getSize());
    asset.setType(MediaAssetType.LEGAL_DOC);
    mediaAssetRepository.save(asset);

    LegalDocument doc = new LegalDocument();
    doc.setClient(client);
    doc.setFileAttachment(asset);
    legalDocumentRepository.save(doc);

    return legalDocumentMapper.toDTO(doc);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LegalDocumentDTO> getByClientId(UUID clientId) {
    return legalDocumentRepository.findAllByClientId(clientId).stream()
        .map(legalDocumentMapper::toDTO)
        .toList();
  }

  @Override
  @Transactional
  public void delete(UUID clientId, UUID docId) {
    LegalDocument doc =
        legalDocumentRepository
            .findById(docId)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado: " + docId));

    if (!doc.getClient().getId().equals(clientId)) {
      throw new IllegalArgumentException("El documento no pertenece al cliente indicado");
    }

    if (doc.getFileAttachment() != null) {
      s3Service.deleteFile(BUCKET, doc.getFileAttachment().getStoragePath());
      mediaAssetRepository.delete(doc.getFileAttachment());
    }

    legalDocumentRepository.delete(doc);
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] download(UUID docId) {
    LegalDocument doc =
        legalDocumentRepository
            .findById(docId)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado: " + docId));

    if (doc.getFileAttachment() == null) {
      throw new IllegalStateException("El documento no tiene archivo adjunto: " + docId);
    }

    return s3Service.downloadFile(BUCKET, doc.getFileAttachment().getStoragePath());
  }

  @Override
  @Transactional(readOnly = true)
  public String getFileName(UUID docId) {
    LegalDocument doc =
        legalDocumentRepository
            .findById(docId)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado: " + docId));

    if (doc.getFileAttachment() == null) return "documento";
    return doc.getFileAttachment().getFilename();
  }
}
