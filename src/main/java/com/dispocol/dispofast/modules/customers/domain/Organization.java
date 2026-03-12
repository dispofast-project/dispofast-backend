package com.dispocol.dispofast.modules.customers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
    name = "organizations",
    indexes = {@Index(name = "idx_organization_legal_name", columnList = "legal_name")})
@Data
@EqualsAndHashCode(callSuper = true)
public class Organization extends Client {

  @Column(name = "legal_name")
  private String legalName;

  @Email
  @Column(name = "billing_email")
  private String billingEmail;

  @Column(name = "rep_first_name")
  private String representativeFirstName;

  @Column(name = "rep_last_name")
  private String representativeLastName;

  @Column(name = "rep_identification")
  private String representativeIdentification;

  @Email
  @Column(name = "rep_email")
  private String representativeEmail;

  @Column(name = "rep_phone")
  private String representativePhone;
}
