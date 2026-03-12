package com.dispocol.dispofast.shared.location.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(
    name = "cities",
    indexes = {
      @Index(name = "idx_city_name", columnList = "name"),
      @Index(name = "idx_city_dept_code", columnList = "department_code")
    })
public class City {
  @Id
  @Column(name = "code", length = 10)
  private String code;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_code", nullable = false)
  private Department department;
}
