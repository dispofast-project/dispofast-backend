package com.dispocol.dispofast.modules.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false, unique = true)
  private String shortDescription;

  @Column(nullable = false, unique = true)
  private String longDescription;

  @Column(nullable = false)
  private String imageUrl;

  @Column(nullable = false)
  private boolean taxFree;

  @Column(nullable = false, unique = true)
  private String sku;

  @Column(nullable = false, unique = true)
  private String reference;

  @Column(nullable = false)
  private String size;

  @Column(nullable = false)
  private String seoTitle;

  @Column(nullable = false)
  private String seoDescription;

  @Column(nullable = false)
  private String seoKeywords;

  @Column(nullable = false)
  private String state;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;
}
