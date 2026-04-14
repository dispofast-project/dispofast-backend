package com.dispocol.dispofast.modules.customers.domain;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "clients")
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Client {
  @Id @GeneratedValue private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "legal_entity_type")
  private LegalEntityType legalEntityType;

  // Cedula o NIT
  @Column(name = "identification_number", unique = true, nullable = false)
  private String identificationNumber;

  @Email
  @Column(name = "email_address", unique = true, nullable = false)
  private String email;

  @Column(name = "phone_number", unique = true, nullable = false)
  private String phone;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "retefuente_applies", nullable = false)
  private Boolean retefuenteApplies = false;

  @Column(name = "address", nullable = false)
  private String address;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "default_advisor_id")
  private AppUser defaultAdvisor;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "city_id")
  private City city;

  @Enumerated(EnumType.STRING)
  @Column(name = "location_zone")
  private LocationZone zone;

  @Column(name = "default_discount_rate")
  private Integer defaultDiscountRate;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "price_list_id")
  private PriceList priceList;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_type_id")
  private ClientType clientType;

  @OneToMany(
      mappedBy = "client",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<LegalDocument> legalDocuments = new ArrayList<>();

  public void addLegalDocument(LegalDocument document) {
    legalDocuments.add(document);
    document.setClient(this);
  }

  public abstract String getDisplayName();
}
