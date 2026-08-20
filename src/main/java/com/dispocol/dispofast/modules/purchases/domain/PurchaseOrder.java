package com.dispocol.dispofast.modules.purchases.domain;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.RetefuenteType;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "purchase_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, unique = true, length = 255)
  private String number;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_condition")
  private PaymentCondition paymentCondition;

  // ── Relaciones ───────────────────────────────────────────────

  /** Proveedor — siempre un cliente ya registrado, nunca un prospecto. */
  @ManyToOne
  @JoinColumn(name = "supplier_id", nullable = false)
  private Client supplier;

  /** Usuario que gestiona la orden de compra (equivalente al seller de una cotización). */
  @ManyToOne
  @JoinColumn(name = "buyer_id", nullable = false)
  private AppUser buyer;

  @OneToMany(
      mappedBy = "purchaseOrder",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<PurchaseOrderItem> items = new ArrayList<>();

  // ── Campos financieros ───────────────────────────────────────

  /** Suma bruta de (cantidad × precio unitario) sin impuestos ni descuentos. */
  @Column(name = "subtotal_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal subtotalAmount = BigDecimal.ZERO;

  /** Tasa del descuento comercial pactado con el proveedor (ej. 0.15 = 15%). */
  @Column(name = "commercial_discount_rate", nullable = false, precision = 7, scale = 4)
  private BigDecimal commercialDiscountRate = BigDecimal.ZERO;

  /** Monto calculado del descuento comercial = subtotal × commercialDiscountRate. */
  @Column(name = "commercial_discount_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal commercialDiscountAmount = BigDecimal.ZERO;

  /** Tasa de otros descuentos adicionales (ej. 0.05 = 5%). */
  @Column(name = "other_discounts_rate", nullable = false, precision = 7, scale = 4)
  private BigDecimal otherDiscountsRate = BigDecimal.ZERO;

  /** Monto calculado de otros descuentos = subtotal × otherDiscountsRate. */
  @Column(name = "other_discounts_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal otherDiscountsAmount = BigDecimal.ZERO;

  /** Tasa del IVA aplicada (ej. 0.19 = 19%). */
  @Column(name = "iva_rate", nullable = false, precision = 7, scale = 4)
  private BigDecimal ivaRate = new BigDecimal("0.19");

  /** Suma de los montos de IVA de todos los ítems. */
  @Column(name = "iva_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal ivaAmount = BigDecimal.ZERO;

  /** Tasa de retefuente que la empresa retiene al pagarle al proveedor (null = no aplica). */
  @Column(name = "retefuente_rate", precision = 7, scale = 4)
  private BigDecimal retefuenteRate;

  /** Monto de retefuente retenido al proveedor (null = no aplica). */
  @Column(name = "retefuente_amount", precision = 18, scale = 2)
  private BigDecimal retefuenteAmount;

  /** Anulación opcional del tipo de retefuente a nivel de orden; null = usar el del proveedor. */
  @Enumerated(EnumType.STRING)
  @Column(name = "retefuente_type_override")
  private RetefuenteType retefuenteTypeOverride;

  /**
   * Total a pagar = subtotal - descuento comercial - otros descuentos + IVA - retefuente + flete.
   */
  @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal totalAmount = BigDecimal.ZERO;

  /** Flete, sumado directamente al total. */
  @Column(name = "freight", nullable = false, precision = 18, scale = 2)
  private BigDecimal freight = BigDecimal.ZERO;

  // ── Auditoría ────────────────────────────────────────────────

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
