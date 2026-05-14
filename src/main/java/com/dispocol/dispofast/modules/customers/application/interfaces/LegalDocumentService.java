package com.dispocol.dispofast.modules.customers.application.interfaces;

import com.dispocol.dispofast.modules.customers.api.dtos.LegalDocumentDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface LegalDocumentService {

  LegalDocumentDTO upload(UUID clientId, MultipartFile file);

  List<LegalDocumentDTO> getByClientId(UUID clientId);

  void delete(UUID clientId, UUID docId);

  byte[] download(UUID docId);

  String getFileName(UUID docId);
}
