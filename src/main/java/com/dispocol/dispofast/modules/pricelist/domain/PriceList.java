package com.dispocol.dispofast.modules.pricelist.domain;

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
@Table(name = "price_lists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceList {
  @Id @GeneratedValue private UUID id;
  private String name;

  @Column(name = "file_key", length = 500)
  private String fileKey;
}
