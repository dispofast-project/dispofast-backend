package com.dispocol.dispofast.modules.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "carriers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carrier {

  @Id @GeneratedValue private UUID id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "website", length = 255)
  private String website;

  @CreationTimestamp
  @Column(name = "registered_at", updatable = false)
  private LocalDate registeredAt;
}
