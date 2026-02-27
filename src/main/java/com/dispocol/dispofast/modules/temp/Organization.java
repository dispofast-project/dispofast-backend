package com.dispocol.dispofast.modules.temp;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.shared.location.domain.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

  @Id @GeneratedValue private UUID id;

  @Column(name = "legal_name")
  private String legalName;

  @Column(name = "default_discount_rate")
  private Integer defaultDiscountRate;

  @Column(name = "address", columnDefinition = "TEXT")
  private String address;

  @Column(name = "billing_email")
  private String billingEmail;

  @Column(name = "general_email")
  private String generalEmail;

  @Column(name = "phone", length = 20)
  private String phone;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "price_list_id")
  private PriceList priceList;

  @ManyToOne(fetch = FetchType.LAZY)
  private Location location;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_sales_rep_id")
  private AppUser assignedSalesRep;
}
