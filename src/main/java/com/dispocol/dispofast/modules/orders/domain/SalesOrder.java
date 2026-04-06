package com.dispocol.dispofast.modules.orders.domain;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
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
  @JoinColumn(name = "client_id")
  private Client client;

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
  private City shipmentCity;

  @Column(name = "shipment_address", columnDefinition = "text")
  private String shipmentAddress;

  @Enumerated(EnumType.STRING)
  @Column(name = "zone", length = 50)
  private LocationZone zone;

  @Column(name = "total_value", precision = 18, scale = 2)
  private BigDecimal totalValue;

  @ManyToOne
  @JoinColumn(name = "price_list_id")
  private PriceList priceList;

  @ManyToOne
  @JoinColumn(name = "quote_id")
  private Quotes quote;

  @Column(name = "invoice_number", length = 50)
  private String invoiceNumber;

  @Column(name = "invoice_url", length = 500)
  private String invoiceUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_condition", length = 30)
  private PaymentCondition paymentCondition;

  @Column(name = "discount_rate")
  private Integer discountRate;

  @Column(name = "additional_discount_rate", precision = 5, scale = 2)
  private BigDecimal additionalDiscountRate;

  @Column(name = "tax_amount", precision = 18, scale = 2)
  private BigDecimal taxAmount;

  @Column(name = "retefuente_amount", precision = 18, scale = 2)
  private BigDecimal retefuenteAmount;

  @Column(name = "reteica_amount", precision = 18, scale = 2)
  private BigDecimal reteicaAmount;

  @Column(name = "freight", precision = 18, scale = 2)
  private BigDecimal freight;
}
