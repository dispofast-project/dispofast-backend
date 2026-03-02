package com.dispocol.dispofast.modules.quotes.domain;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import com.dispocol.dispofast.modules.temp.account.Person;
import com.dispocol.dispofast.shared.location.domain.Location;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
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

  @Column(name = "subtotal_amount", nullable = false)
  private double subtotalAmount;

  @Column(name = "discount_total", nullable = false)
  private double discountTotal;

  @Column(name = "tax_total", nullable = false)
  private double taxTotal;

  @Column(name = "total_amount", nullable = false)
  private double totalAmount;

  @Column(name = "expiration_date", nullable = false)
  private OffsetDateTime expirationDate;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Person account;

  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private AppUser seller;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @ManyToOne
  @JoinColumn(name = "price_list_id", nullable = false)
  private PriceList priceList;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
