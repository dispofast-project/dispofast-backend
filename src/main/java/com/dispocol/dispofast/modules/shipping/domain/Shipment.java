package com.dispocol.dispofast.modules.shipping.domain;

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
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "shipments")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Shipment {

  @Id @GeneratedValue @EqualsAndHashCode.Include private UUID id;

  @Column(name = "invoice_id", nullable = false)
  private UUID invoiceId;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", length = 20, nullable = false)
  private ShipmentState state = ShipmentState.PENDING;

  @Column(name = "delivery_address", length = 500, nullable = false)
  private String deliveryAddress;

  @Column(name = "address_detail", length = 255)
  private String addressDetail;

  @Column(name = "departure_date")
  private OffsetDateTime departureDate;

  @Column(name = "delivery_date")
  private OffsetDateTime deliveryDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "carrier_id")
  private Carrier carrier;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_id")
  private Driver driver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "city_code")
  private City city;

  @Column(name = "invoice_number", length = 50)
  private String invoiceNumber;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "client_name", length = 255)
  private String clientName;

  @Column(name = "asesor_name", length = 255)
  private String asesorName;

  @Column(name = "estimated_delivery_date")
  private LocalDate estimatedDeliveryDate;

  @Column(name = "product_count")
  private Integer productCount;

  @Column(name = "delivery_type", length = 20)
  private String deliveryType;

  @Column(name = "tracking_code", length = 100)
  private String trackingCode;
}
