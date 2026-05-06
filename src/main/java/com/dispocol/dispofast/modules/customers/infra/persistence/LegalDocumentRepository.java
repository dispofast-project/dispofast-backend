package com.dispocol.dispofast.modules.customers.infra.persistence;

import com.dispocol.dispofast.modules.customers.domain.LegalDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalDocumentRepository extends JpaRepository<LegalDocument, UUID> {
  List<LegalDocument> findAllByClientId(UUID clientId);
}
