package com.dispocol.dispofast.modules.pricelist.api.controllers;

import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListResponseDTO;
import com.dispocol.dispofast.modules.pricelist.infra.persistence.PriceListRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/price-lists")
@RequiredArgsConstructor
public class PriceListController {

  private final PriceListRepository priceListRepository;

  @GetMapping
  public ResponseEntity<List<PriceListResponseDTO>> getAllPriceLists() {
    List<PriceListResponseDTO> priceLists =
        priceListRepository.findAll().stream()
            .map(pl -> new PriceListResponseDTO(pl.getId(), pl.getName()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(priceLists);
  }
}
