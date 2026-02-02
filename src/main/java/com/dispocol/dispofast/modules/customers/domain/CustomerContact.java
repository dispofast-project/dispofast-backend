package com.dispocol.dispofast.modules.customers.domain;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "customer_contacts")
@Data
@AllArgsConstructor
public class CustomerContact {
    
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "contact_name", nullable = false, length = 255)
    private String contactName;

    @Column(name = "contact_last_name", nullable = false, length = 255)
    private String contactLastName;

    @Column(name = "position", length = 255)
    private String position;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
