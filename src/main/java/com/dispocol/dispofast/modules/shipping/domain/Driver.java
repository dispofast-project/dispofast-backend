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
@Table(name = "drivers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

  @Id @GeneratedValue private UUID id;

  @Column(name = "name", length = 150, nullable = false)
  private String name;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "cedula", length = 30)
  private String cedula;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDate createdAt;
}
