package com.dispocol.dispofast.modules.temp;

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
}
