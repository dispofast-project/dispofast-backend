package com.dispocol.dispofast.modules.cartera.domain;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_receipt")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReceipt {

  @Id private UUID id;

  @Column(name = "receipt_code", nullable = false, unique = true, length = 30)
  private String receiptCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ar_entry_id", nullable = false)
  private ArEntry arEntry;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private AppUser createdBy;

  @Column(name = "document_number", length = 50)
  private String documentNumber;

  @Column(name = "payment_date", nullable = false)
  private LocalDate paymentDate;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal value;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false, length = 20)
  private PaymentMethod paymentMethod;

  @Column(name = "voucher_s3_key", length = 500)
  private String voucherS3Key;

  @Column(columnDefinition = "TEXT")
  private String observations;

  /** Porcentaje de descuento por pronto pago aplicado (2, 3 o 5), si el cajero lo seleccionó. */
  @Column(name = "prompt_payment_discount_rate")
  private Integer promptPaymentDiscountRate;

  /**
   * Monto del descuento por pronto pago: se calcula sobre el subtotal antes de impuestos de la
   * orden asociada y se suma al valor en efectivo para abonar al saldo de la cartera.
   */
  @Column(name = "prompt_payment_discount_amount", precision = 18, scale = 2)
  private BigDecimal promptPaymentDiscountAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentReceiptState state = PaymentReceiptState.ACTIVE;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    receiptCode = id.toString().replace("-", "").substring(0, 13);
    createdAt = OffsetDateTime.now();
  }
}
