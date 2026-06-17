package com.dispocol.dispofast.modules.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carriers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carrier {

  @Id @GeneratedValue private UUID id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "plate", length = 50, nullable = false, unique = true)
  private String plate;
}
