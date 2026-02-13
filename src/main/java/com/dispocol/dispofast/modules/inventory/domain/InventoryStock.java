package com.dispocol.dispofast.modules.inventory.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory_stock")
public class InventoryStock {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(nullable = false, columnDefinition = "int default 0")
    private int quantityAvailable;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int quantityReserved;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private String state;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @PrePersist
    public void prePersist() {
        this.lastUpdated = LocalDateTime.now();
    }

}
