package com.dispocol.dispofast.modules.orders.domain;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.temp.Account;
import com.dispocol.dispofast.modules.temp.PriceList;
import com.dispocol.dispofast.shared.location.domain.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "sales_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrder {

  @Id @GeneratedValue private UUID id;

  @Column(name = "order_number", length = 20)
  private String orderNumber;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

  @ManyToOne
  @JoinColumn(name = "asesor_user_id")
  private AppUser asesor;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", length = 20)
  private OrderState state;

  @Column(name = "order_date")
  private OffsetDateTime orderDate;

  @ManyToOne
  @JoinColumn(name = "shipment_city_id")
  private Location shipmentCity;

  @Column(name = "shipment_address", columnDefinition = "text")
  private String shipmentAddress;

  @Column(name = "zone", length = 50)
  private String zone;

  @Column(name = "total_value", precision = 18, scale = 2)
  private BigDecimal totalValue;

  @ManyToOne
  @JoinColumn(name = "account_price_list_id")
  private PriceList priceList;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private AppUser user;

  @ManyToOne
  @JoinColumn(name = "quote_id")
  private Quotes quote;

  @Column(name = "invoice_number", length = 50)
  private String invoiceNumber;

  @Column(name = "invoice_url", length = 500)
  private String invoiceUrl;
}
