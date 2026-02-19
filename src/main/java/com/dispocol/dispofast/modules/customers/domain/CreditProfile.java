package com.dispocol.dispofast.modules.customers.domain;

import jakarta.persistence.*;
import java.sql.Date;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "credit_profiles")
@Data
public class CreditProfile {

  @Id @GeneratedValue private UUID id;

  @Column(name = "credit_days", nullable = false)
  private int creditDays;

  @Column(name = "credit_limit", nullable = false)
  private double creditLimit;

  @Column(name = "approved", nullable = false)
  private boolean approved;

  @Column(name = "approver", length = 255)
  private String approver;

  @Column(name = "approval_date")
  private Date approvalDate;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;
}
