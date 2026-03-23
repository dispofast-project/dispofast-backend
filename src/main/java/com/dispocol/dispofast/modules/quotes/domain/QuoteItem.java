package com.dispocol.dispofast.modules.quotes.domain;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quote_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteItem {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quote_id", nullable = false)
  private Quotes quote;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal quantity;

  @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal unitPrice;

  @Column(name = "discount_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal discountAmount;

  @Column(name = "tax_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal taxAmount;

  @Column(name = "line_total", nullable = false, precision = 18, scale = 2)
  private BigDecimal lineTotal;
}
