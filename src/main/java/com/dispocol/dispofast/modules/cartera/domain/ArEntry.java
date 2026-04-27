package com.dispocol.dispofast.modules.cartera.domain;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import com.dispocol.dispofast.shared.location.domain.City;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ar_entries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArEntry {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asesor_user_id")
  private AppUser asesor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private SalesOrder order;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ArEntryState state = ArEntryState.PENDING;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal value;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id")
  private Invoice invoice;

  @Column(name = "payment_term_days", nullable = false)
  private int paymentTermDays = 30;

  @Column(name = "expiration_date", nullable = false)
  private OffsetDateTime expirationDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "city_id")
  private City city;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ArEntrySource source = ArEntrySource.ORDER;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  void prePersist() {
    createdAt = OffsetDateTime.now();
  }

  /** Días transcurridos desde la fecha de factura hasta hoy. */
  public long getDiasCartera() {
    if (invoice == null || invoice.getIssueDate() == null) return 0;
    return ChronoUnit.DAYS.between(invoice.getIssueDate().toLocalDate(), LocalDate.now());
  }

  /** Días transcurridos desde la fecha de vencimiento hasta hoy. Retorna 0 si aún no ha vencido. */
  public long getDiasVencimiento() {
    LocalDate today = LocalDate.now();
    LocalDate expiry = expirationDate.toLocalDate();
    return today.isAfter(expiry) ? ChronoUnit.DAYS.between(expiry, today) : 0;
  }
}
