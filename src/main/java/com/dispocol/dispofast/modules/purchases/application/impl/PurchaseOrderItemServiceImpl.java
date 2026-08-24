package com.dispocol.dispofast.modules.purchases.application.impl;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;
import com.dispocol.dispofast.modules.purchases.api.dtos.AddPurchaseOrderItemRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderItemResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.UpdatePurchaseOrderItemRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.mappers.PurchaseOrderItemMapper;
import com.dispocol.dispofast.modules.purchases.application.interfaces.PurchaseOrderItemService;
import com.dispocol.dispofast.modules.purchases.domain.PurchaseOrder;
import com.dispocol.dispofast.modules.purchases.domain.PurchaseOrderItem;
import com.dispocol.dispofast.modules.purchases.infra.persistence.PurchaseOrderItemRepository;
import com.dispocol.dispofast.modules.purchases.infra.persistence.PurchaseOrderRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.params.infra.persistence.SystemParamRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderItemServiceImpl implements PurchaseOrderItemService {

  private final PurchaseOrderItemRepository purchaseOrderItemRepository;
  private final PurchaseOrderRepository purchaseOrderRepository;
  private final ProductRepository productRepository;
  private final PurchaseOrderItemMapper purchaseOrderItemMapper;
  private final PurchaseOrderServiceImpl purchaseOrderService;
  private final SystemParamRepository systemParamRepository;

  @Override
  @Transactional
  public PurchaseOrderItemResponseDTO addItem(
      UUID purchaseOrderId, AddPurchaseOrderItemRequestDTO dto) {
    PurchaseOrder order = findOrder(purchaseOrderId);
    Product product = findProduct(dto.getProductId());

    PurchaseOrderItem item =
        purchaseOrderItemRepository
            .findByPurchaseOrder_IdAndProduct_Id(purchaseOrderId, product.getId())
            .map(
                existing -> {
                  existing.setQuantity(existing.getQuantity().add(dto.getQuantity()));
                  recalculateItemAmounts(existing);
                  return existing;
                })
            .orElseGet(
                () ->
                    buildItem(
                        order,
                        product,
                        dto.getQuantity(),
                        dto.getUnitPrice() != null ? dto.getUnitPrice() : BigDecimal.ZERO));

    PurchaseOrderItem saved = purchaseOrderItemRepository.save(item);

    purchaseOrderService.recalculatePurchaseOrderTotals(order);
    purchaseOrderRepository.save(order);

    return purchaseOrderItemMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional
  public PurchaseOrderItemResponseDTO updateItem(
      UUID purchaseOrderId, UUID itemId, UpdatePurchaseOrderItemRequestDTO dto) {
    PurchaseOrder order = findOrder(purchaseOrderId);
    PurchaseOrderItem item =
        purchaseOrderItemRepository
            .findById(itemId)
            .filter(i -> i.getPurchaseOrder().getId().equals(purchaseOrderId))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "PurchaseOrderItem not found with id: " + itemId));

    if (dto.getQuantity() != null) item.setQuantity(dto.getQuantity());
    if (dto.getUnitPrice() != null) item.setUnitPrice(dto.getUnitPrice());

    recalculateItemAmounts(item);
    PurchaseOrderItem saved = purchaseOrderItemRepository.save(item);

    purchaseOrderService.recalculatePurchaseOrderTotals(order);
    purchaseOrderRepository.save(order);

    return purchaseOrderItemMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional
  public void removeItem(UUID purchaseOrderId, UUID itemId) {
    PurchaseOrder order = findOrder(purchaseOrderId);
    PurchaseOrderItem item =
        purchaseOrderItemRepository
            .findById(itemId)
            .filter(i -> i.getPurchaseOrder().getId().equals(purchaseOrderId))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "PurchaseOrderItem not found with id: " + itemId));

    purchaseOrderItemRepository.delete(item);
    purchaseOrderService.recalculatePurchaseOrderTotals(order);
    purchaseOrderRepository.save(order);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PurchaseOrderItemResponseDTO> getItems(UUID purchaseOrderId) {
    findOrder(purchaseOrderId);
    return purchaseOrderItemMapper.toResponseDTOList(
        purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrderId));
  }

  // ── Helpers ──────────────────────────────────────────────────

  private PurchaseOrderItem buildItem(
      PurchaseOrder order, Product product, BigDecimal quantity, BigDecimal unitPrice) {
    PurchaseOrderItem item = new PurchaseOrderItem();
    item.setPurchaseOrder(order);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setUnitPrice(unitPrice);
    recalculateItemAmounts(item);
    return item;
  }

  private void recalculateItemAmounts(PurchaseOrderItem item) {
    BigDecimal ivaRate =
        systemParamRepository
            .findByClave("IVA")
            .map(p -> p.getValor())
            .orElse(new BigDecimal("0.19"));
    BigDecimal taxRate =
        Boolean.TRUE.equals(item.getProduct().isTaxFree()) ? BigDecimal.ZERO : ivaRate;
    BigDecimal gross = item.getUnitPrice().multiply(item.getQuantity());
    BigDecimal taxAmount = gross.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

    item.setTaxRate(taxRate);
    item.setTaxAmount(taxAmount);
    item.setLineTotal(gross.add(taxAmount).setScale(2, RoundingMode.HALF_UP));
  }

  private PurchaseOrder findOrder(UUID purchaseOrderId) {
    return purchaseOrderRepository
        .findById(purchaseOrderId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Purchase order not found with id: " + purchaseOrderId));
  }

  private Product findProduct(UUID productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Product not found with id: " + productId));
  }
}
