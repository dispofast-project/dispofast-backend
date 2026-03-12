package com.dispocol.dispofast.modules.customers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientType {

  @Id @GeneratedValue private Long id;

  @Column(name = "name", nullable = false)
  private String name;
}
