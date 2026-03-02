package com.dispocol.dispofast.modules.customers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "organizations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Organization extends Client {

  @Column(name = "legal_name")
  private String legalName;

  @Email
  @Column(name = "billing_email")
  private String billingEmail;
}
