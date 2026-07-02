package com.dispocol.dispofast.modules.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

  @Id @GeneratedValue private UUID id;

  @Column(name = "plate", length = 50, nullable = false, unique = true)
  private String plate;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 30)
  private VehicleState state;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 30)
  private VehicleType type;

  @Column(name = "created_at", updatable = false)
  private LocalDate createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDate.now();
  }
}
