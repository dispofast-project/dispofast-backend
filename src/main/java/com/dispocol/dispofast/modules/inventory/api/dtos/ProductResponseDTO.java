package com.dispocol.dispofast.modules.inventory.api.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

  private UUID id;
  private String name;
  private String shortDescription;
  private String longDescription;
  private String imageUrl;
  private boolean taxFree;
  private String sku;
  private String reference;
  private String size;
  private String seoTitle;
  private String seoDescription;
  private String seoKeywords;
  private String state;

  private String categoryName;
}
