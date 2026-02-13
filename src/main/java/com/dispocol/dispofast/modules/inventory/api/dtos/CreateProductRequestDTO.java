package com.dispocol.dispofast.modules.inventory.api.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequestDTO {
    
    @NotBlank(message = "Nombre es requerido")
    private String name;

    @NotBlank(message = "Descripción corta es requerida")
    private String shortDescription;

    @NotBlank(message = "Descripción larga es requerida")
    private String longDescription;

    @NotBlank(message = "URL de la imagen es requerida")
    private String imageUrl;

    @NotNull(message = "Indicador de libre de impuestos es requerido")
    private Boolean taxFree;

    @NotBlank(message = "SKU es requerido")
    private String sku;

    @NotBlank(message = "Referencia es requerida")
    private String reference;

    @NotBlank(message = "Tamaño es requerido")
    private String size;

    @NotBlank(message = "Título SEO es requerido")
    private String seoTitle;

    @NotBlank(message = "Descripción SEO es requerida")
    private String seoDescription;

    @NotBlank(message = "Palabras clave SEO son requeridas")
    private String seoKeywords;

    @NotBlank(message = "Estado es requerido")
    private String state;

    @NotNull(message = "ID de categoría es requerido")
    private UUID categoryId;

    private int initialStock;

}
