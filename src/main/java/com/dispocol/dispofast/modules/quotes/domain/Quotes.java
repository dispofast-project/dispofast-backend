package com.dispocol.dispofast.modules.quotes.domain;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
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
@Table(name = "quotes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quotes {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, unique = true, length = 255)
  private String number;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private QuoteStatus status;

  @Column(name = "expiration_date", nullable = false)
  private OffsetDateTime expirationDate;

  // ── Relaciones ───────────────────────────────────────────────

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Client account;

  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private AppUser seller;

  @ManyToOne
  @JoinColumn(name = "city_id", nullable = false)
  private City city;

  @Enumerated(EnumType.STRING)
  @Column(name = "location_zone")
  private LocationZone zone;

  @ManyToOne
  @JoinColumn(name = "price_list_id", nullable = false)
  private PriceList priceList;

  @OneToMany(
      mappedBy = "quote",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<QuoteItem> items = new ArrayList<>();

  // ── Campos financieros ───────────────────────────────────────

  /** Suma bruta de (cantidad × precio unitario) sin impuestos ni descuentos. */
  @Column(name = "subtotal_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal subtotalAmount = BigDecimal.ZERO;

  /** Tasa del descuento comercial (ej. 0.15 = 15%). Editable por cotización. */
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

  /** Tasa de retefuente (null para personas naturales). */
  @Column(name = "retefuente_rate", precision = 7, scale = 4)
  private BigDecimal retefuenteRate;

  /** Monto de retefuente (null para personas naturales). */
  @Column(name = "retefuente_amount", precision = 18, scale = 2)
  private BigDecimal retefuenteAmount;

  /** Tasa de reteica (null para personas naturales). */
  @Column(name = "reteica_rate", precision = 7, scale = 4)
  private BigDecimal reteicaRate;

  /** Monto de reteica (null para personas naturales). */
  @Column(name = "reteica_amount", precision = 18, scale = 2)
  private BigDecimal reteicaAmount;

  /**
   * Total a pagar = subtotal - descuento comercial - otros descuentos + IVA - retefuente - reteica.
   */
  @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal totalAmount = BigDecimal.ZERO;

  // ── Auditoría ────────────────────────────────────────────────

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
