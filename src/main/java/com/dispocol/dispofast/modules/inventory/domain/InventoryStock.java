package com.dispocol.dispofast.modules.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory_stock")
public class InventoryStock {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, columnDefinition = "int default 0")
  private int quantityAvailable;

  @Column(nullable = false, columnDefinition = "int default 0")
  private int quantityReserved;

  @Column(nullable = false)
  private LocalDateTime lastUpdated;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private StockState state;

  @OneToOne
  @JoinColumn(name = "product_id", nullable = false, unique = true)
  private Product product;

  @PrePersist
  @PreUpdate
  public void refreshLastUpdated() {
    this.lastUpdated = LocalDateTime.now();
  }
}
