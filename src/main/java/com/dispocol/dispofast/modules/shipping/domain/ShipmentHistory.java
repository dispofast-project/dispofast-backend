package com.dispocol.dispofast.modules.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipment_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentHistory {

  @Id @GeneratedValue private UUID id;

  @Column(name = "shipment_id", nullable = false)
  private UUID shipmentId;

  @Column(name = "changed_at", nullable = false)
  private OffsetDateTime changedAt;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "user_email", nullable = false, length = 255)
  private String userEmail;
}
