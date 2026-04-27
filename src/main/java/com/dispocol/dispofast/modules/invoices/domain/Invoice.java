package com.dispocol.dispofast.modules.invoices.domain;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sales_order_id")
  private SalesOrder salesOrder;

  @Column(name = "invoice_number", length = 50, nullable = false)
  private String invoiceNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id")
  private Client client;

  @Column(name = "issue_date", nullable = false)
  private OffsetDateTime issueDate;

  @Column(name = "total_value", precision = 18, scale = 2, nullable = false)
  private BigDecimal totalValue;

  @Column(name = "pdf_s3_key", length = 500)
  private String pdfS3Key;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", length = 20, nullable = false)
  private InvoiceState state = InvoiceState.ISSUED;
}
