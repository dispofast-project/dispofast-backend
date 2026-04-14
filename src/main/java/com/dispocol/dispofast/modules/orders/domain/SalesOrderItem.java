package com.dispocol.dispofast.modules.orders.domain;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_order_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrderItem {

  @Id @GeneratedValue private UUID id;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private SalesOrder order;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "quantity", precision = 12, scale = 2)
  private BigDecimal quantity;

  @Column(name = "unit_price", precision = 18, scale = 2)
  private BigDecimal unitPrice;

  @Column(name = "discount", precision = 18, scale = 2)
  private BigDecimal discount;

  @Column(name = "line_total", precision = 18, scale = 2)
  private BigDecimal lineTotal;
}
