package com.dispocol.dispofast.shared.location.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(
    name = "departments",
    indexes = {@Index(name = "idx_dept_name", columnList = "name", unique = true)})
public class Department {
  @Id
  @Column(name = "code", length = 5)
  private String code;

  @Column(nullable = false)
  private String name;
}
