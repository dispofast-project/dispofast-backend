package com.dispocol.dispofast.modules.temp.account;

import com.dispocol.dispofast.modules.temp.MediaAsset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_attachment_id")
  private MediaAsset fileAttachment;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private OffsetDateTime updatedAt;

  @Column(name = "created_at")
  @CreationTimestamp
  private OffsetDateTime createdAt;
}
