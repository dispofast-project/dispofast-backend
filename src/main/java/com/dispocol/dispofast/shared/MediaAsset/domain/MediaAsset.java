package com.dispocol.dispofast.shared.MediaAsset.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "media_assets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaAsset {

  @Id @GeneratedValue private UUID id;

  @Column(name = "filename", length = 255)
  private String filename;

  @Column(name = "storage_path", columnDefinition = "TEXT")
  private String storagePath;

  @Column(name = "mime_type", length = 100)
  private String mimeType;

  @Column(name = "file_size")
  private Long fileSize;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private MediaAssetType type;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata", columnDefinition = "jsonb", nullable = true)
  private String metadata;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
