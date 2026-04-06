package com.dispocol.dispofast.modules.pricelist.application.impl;

import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;
import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListItemDTO;
import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListResponseDTO;
import com.dispocol.dispofast.modules.pricelist.application.interfaces.PriceListService;
import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import com.dispocol.dispofast.modules.pricelist.domain.PriceListItem;
import com.dispocol.dispofast.modules.pricelist.infra.persistence.PriceListItemRepository;
import com.dispocol.dispofast.modules.pricelist.infra.persistence.PriceListRepository;
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceListServiceImpl implements PriceListService {

  static final String BUCKET = "dispofast-price-lists";
  private static final int DATA_START_ROW_INDEX = 9; // row 10 (0-indexed)

  private final PriceListRepository priceListRepository;
  private final PriceListItemRepository priceListItemRepository;
  private final ProductRepository productRepository;
  private final S3Service s3Service;

  @Override
  public List<PriceListResponseDTO> getAllPriceLists() {
    return priceListRepository.findAll().stream()
        .map(pl -> new PriceListResponseDTO(pl.getId(), pl.getName(), pl.getFileKey() != null))
        .toList();
  }

  @Override
  public List<PriceListItemDTO> getItemsByPriceList(UUID priceListId) {
    return priceListItemRepository.findByPriceList_Id(priceListId).stream()
        .map(
            item ->
                new PriceListItemDTO(
                    item.getProduct().getId(),
                    item.getProduct().getReference(),
                    item.getUnitPrice()))
        .toList();
  }

  @Override
  @Transactional
  public void uploadPriceListItems(UUID priceListId, MultipartFile file) {
    PriceList priceList =
        priceListRepository
            .findById(priceListId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException("Lista de precios no encontrada: " + priceListId));

    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);
      List<PriceListItem> items = new ArrayList<>();

      for (int rowIndex = DATA_START_ROW_INDEX; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) continue;

        Cell codeCell = row.getCell(0); // Col A: product reference
        Cell priceCell = row.getCell(2); // Col C: unit price sin IVA

        if (codeCell == null || priceCell == null) continue;

        String reference = getCellStringValue(codeCell).trim();
        if (reference.isBlank()) continue;

        BigDecimal unitPrice = getCellNumericValue(priceCell);
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) continue;

        productRepository
            .findByReference(reference)
            .ifPresentOrElse(
                product -> {
                  PriceListItem item =
                      priceListItemRepository
                          .findByPriceList_IdAndProduct_Reference(priceListId, reference)
                          .orElse(new PriceListItem());
                  item.setPriceList(priceList);
                  item.setProduct(product);
                  item.setUnitPrice(unitPrice);
                  items.add(item);
                },
                () -> log.warn("Product with reference '{}' not found, skipping", reference));
      }

      priceListItemRepository.saveAll(items);
      log.info("Uploaded {} price list items for price list {}", items.size(), priceListId);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Error al procesar el archivo Excel: " + e.getMessage(), e);
    }

    // Store the original file in S3
    String fileKey = priceListId + "/" + file.getOriginalFilename();
    try {
      s3Service.uploadFile(
          BUCKET,
          fileKey,
          file.getInputStream(),
          file.getContentType(),
          file.getSize());
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Error al guardar el archivo en S3: " + e.getMessage(), e);
    }
    priceList.setFileKey(fileKey);
    priceListRepository.save(priceList);
  }

  @Override
  public byte[] downloadPriceListFile(UUID priceListId) {
    PriceList priceList =
        priceListRepository
            .findById(priceListId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException("Lista de precios no encontrada: " + priceListId));
    if (priceList.getFileKey() == null) {
      throw new IllegalStateException(
          "La lista de precios no tiene un archivo asociado: " + priceListId);
    }
    return s3Service.downloadFile(BUCKET, priceList.getFileKey());
  }

  @Override
  public String getPriceListOriginalFileName(UUID priceListId) {
    PriceList priceList =
        priceListRepository
            .findById(priceListId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException("Lista de precios no encontrada: " + priceListId));
    if (priceList.getFileKey() == null) {
      return "price-list.xlsx";
    }
    String key = priceList.getFileKey();
    return key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
  }

  @Override
  public Optional<BigDecimal> resolveUnitPrice(UUID priceListId, String reference) {
    return priceListItemRepository
        .findByPriceList_IdAndProduct_Reference(priceListId, reference)
        .map(PriceListItem::getUnitPrice);
  }

  private String getCellStringValue(Cell cell) {
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> {
        double val = cell.getNumericCellValue();
        yield val == Math.floor(val) ? String.valueOf((long) val) : String.valueOf(val);
      }
      default -> "";
    };
  }

  private BigDecimal getCellNumericValue(Cell cell) {
    return switch (cell.getCellType()) {
      case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
      case STRING -> {
        try {
          yield new BigDecimal(cell.getStringCellValue().replace(",", ".").trim());
        } catch (NumberFormatException e) {
          yield null;
        }
      }
      default -> null;
    };
  }
}
