package com.dispocol.dispofast.modules.customers.domain;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "customer_legal_docs")
@Data
@AllArgsConstructor
public class CustomerLegalDoc {
    
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "doc_type", length = 100)
    private String docType;

    @Column(name = "issued_at", length = 255)
    private Date issueDate;

    @Column(name = "expires_at", length = 255)
    private Date expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
