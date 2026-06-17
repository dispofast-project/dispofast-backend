package com.dispocol.dispofast.modules.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipment_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentItem {

  @Id @GeneratedValue private UUID id;

  @Column(name = "shipment_id", nullable = false)
  private UUID shipmentId;

  @Column(name = "product_id")
  private UUID productId;

  @Column(name = "product_sku", length = 100)
  private String productSku;

  @Column(name = "product_name", length = 500, nullable = false)
  private String productName;

  @Column(name = "quantity", precision = 12, scale = 2, nullable = false)
  private BigDecimal quantity;

  @Column(name = "unit_price", precision = 18, scale = 2, nullable = false)
  private BigDecimal unitPrice;

  @Column(name = "tax_percent", nullable = false)
  private Integer taxPercent = 0;

  @Column(name = "line_total", precision = 18, scale = 2, nullable = false)
  private BigDecimal lineTotal;
}
