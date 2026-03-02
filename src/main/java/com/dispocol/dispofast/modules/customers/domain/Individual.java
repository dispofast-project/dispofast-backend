package com.dispocol.dispofast.modules.customers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "individuals")
@Data
@EqualsAndHashCode(callSuper = true)
public class Individual extends Client {

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "rep_first_name")
  private String representativeFirstName;

  @Column(name = "rep_last_name")
  private String representativeLastName;

  @Column(name = "rep_identification")
  private String representativeIdentification;

  @Column(name = "rep_job_title")
  private String representativeJobTitle;

  @Email
  @Column(name = "rep_email")
  private String representativeEmail;

  @Column(name = "rep_phone")
  private String representativePhone;
}
