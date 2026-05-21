package com.dispocol.dispofast.shared.params.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "system_params")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemParam {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, length = 50)
  private String clave;

  @Column(nullable = false, precision = 18, scale = 4)
  private BigDecimal valor;

  @UpdateTimestamp
  @Column(name = "last_updated", nullable = false)
  private OffsetDateTime lastUpdated;
}
