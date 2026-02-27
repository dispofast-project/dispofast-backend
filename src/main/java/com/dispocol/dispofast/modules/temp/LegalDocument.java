package com.dispocol.dispofast.modules.temp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "legal_documents")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LegalDocument {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private Person owner;

  @Column(name = "document_type")
  @Enumerated(EnumType.STRING)
  private LegalDocumentType documentType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_attachment_id")
  private MediaAsset fileAttachment;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private OffsetDateTime updatedAt;

  @Column(name = "created_at")
  @CreationTimestamp
  private OffsetDateTime createdAt;

  @Column(name = "expires_at", nullable = true)
  private OffsetDateTime expiresAt;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private LegalDocumentStatus status = LegalDocumentStatus.ACTIVE;
}
