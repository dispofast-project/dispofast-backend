package com.dispocol.dispofast.modules.customers.domain;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;

import com.dispocol.dispofast.modules.iam.domain.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "type_person", nullable = false, length = 100)
    private String typePerson;

    @Column(name = "social_reason", nullable = false, length = 255)
    private String socialReason;

    @Column(name = "nit_cedula", nullable = false, length = 100)
    private String nitCedula;

    @Column(name = "witholding_tax", nullable = false)
    private boolean witholdingTax;

    @Column(name = "zone", length = 100)
    private String zone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "depto", length = 100)
    private String depto;
    
    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "email_fact_elec", length = 255)
    private String emailFactElec;

    @Column(name = "classification", length = 100)
    private String classification;

    @Column(name = "type_client", length = 100)
    private String typeClient;

    @Column(name = "origin", length = 100)
    private String origin;

    @Column(name = "state", length = 100)
    private String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CustomerContact> contacts;
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CustomerLegalDoc> legalDocs;
    
    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
    private CreditProfile creditProfile;

}
