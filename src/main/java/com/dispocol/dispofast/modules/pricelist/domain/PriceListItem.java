package com.dispocol.dispofast.modules.pricelist.domain;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "price_list_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"price_list_id", "product_id"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceListItem {

  @Id @GeneratedValue private UUID id;

  @ManyToOne
  @JoinColumn(name = "price_list_id", nullable = false)
  private PriceList priceList;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "unit_price", precision = 18, scale = 2, nullable = false)
  private BigDecimal unitPrice;
}
