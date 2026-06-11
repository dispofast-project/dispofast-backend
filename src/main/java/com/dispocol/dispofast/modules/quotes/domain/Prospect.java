package com.dispocol.dispofast.modules.quotes.domain;

import com.dispocol.dispofast.modules.customers.domain.ClientType;
import com.dispocol.dispofast.modules.customers.domain.LegalEntityType;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prospects")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prospect {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, length = 255)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "legal_entity_type", nullable = false, length = 50)
  private LegalEntityType legalEntityType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_type_id")
  private ClientType clientType;

  @Column(length = 50)
  private String phone;

  @Column(length = 255)
  private String email;
}
