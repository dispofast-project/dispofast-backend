package com.dispocol.dispofast.modules.iam.domain;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_product_allocations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProductAllocation {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  /** Cupo total fijado por el admin para este vendedor y producto. */
  @Column(name = "assigned_quantity", nullable = false)
  private int assignedQuantity;

  /** Cuánto de ese cupo está comprometido en órdenes activas del vendedor. */
  @Column(name = "consumed_quantity", nullable = false)
  private int consumedQuantity = 0;
}
