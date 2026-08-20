package com.dispocol.dispofast.modules.purchases.application.impl;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.RetefuenteType;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.purchases.api.dtos.CreatePurchaseOrderRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderPreviewResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.UpdatePurchaseOrderRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.mappers.PurchaseOrderMapper;
import com.dispocol.dispofast.modules.purchases.application.interfaces.PurchaseOrderService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

  private final PurchaseOrderRepository purchaseOrderRepository;
  private final PurchaseOrderItemRepository purchaseOrderItemRepository;
  private final PurchaseOrderMapper purchaseOrderMapper;
  private final ClientRepository clientRepository;
  private final UserRepository userRepository;
  private final SystemParamRepository systemParamRepository;

  @Override
  @Transactional
  public PurchaseOrderResponseDTO createPurchaseOrder(CreatePurchaseOrderRequestDTO dto) {
    if (dto.getSupplierId() == null) {
      throw new IllegalArgumentException("Debe proporcionar el proveedor (supplierId).");
    }

    Client supplier =
        clientRepository
            .findById(dto.getSupplierId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Client not found with id: " + dto.getSupplierId()));

    AppUser currentUser = currentUser();

    PurchaseOrder order = new PurchaseOrder();
    order.setSupplier(supplier);
    order.setBuyer(currentUser);
    order.setNumber("OC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

    applyDefaultFinancials(order);

    if (supplier.getRetefuenteType() != null
        && supplier.getRetefuenteType() != RetefuenteType.NO_APLICA) {
      order.setRetefuenteRate(retefuenteRateFor(supplier.getRetefuenteType()));
      order.setRetefuenteAmount(BigDecimal.ZERO);
    }

    return purchaseOrderMapper.toResponseDTO(purchaseOrderRepository.save(order));
  }

  private void applyDefaultFinancials(PurchaseOrder order) {
    BigDecimal ivaRate =
        systemParamRepository
            .findByClave("IVA")
            .map(p -> p.getValor())
            .orElse(new BigDecimal("0.19"));
    order.setIvaRate(ivaRate);
    order.setCommercialDiscountRate(BigDecimal.ZERO);
    order.setSubtotalAmount(BigDecimal.ZERO);
    order.setCommercialDiscountAmount(BigDecimal.ZERO);
    order.setOtherDiscountsRate(BigDecimal.ZERO);
    order.setOtherDiscountsAmount(BigDecimal.ZERO);
    order.setIvaAmount(BigDecimal.ZERO);
    order.setRetefuenteAmount(null);
    order.setRetefuenteRate(null);
    order.setTotalAmount(BigDecimal.ZERO);
    order.setFreight(BigDecimal.ZERO);
  }

  @Override
  @Transactional
  public PurchaseOrderResponseDTO getPurchaseOrderById(UUID id) {
    PurchaseOrder order = findOrder(id);
    recalculatePurchaseOrderTotals(order);
    return purchaseOrderMapper.toResponseDTO(purchaseOrderRepository.save(order));
  }

  @Override
  @Transactional
  public PurchaseOrderResponseDTO updatePurchaseOrder(UUID id, UpdatePurchaseOrderRequestDTO dto) {
    PurchaseOrder order = findOrder(id);
    purchaseOrderMapper.updateEntityFromDTO(dto, order);
    if (dto.getBuyerId() != null) {
      order.setBuyer(userRepository.getReferenceById(dto.getBuyerId()));
    }
    recalculatePurchaseOrderTotals(order);
    return purchaseOrderMapper.toResponseDTO(purchaseOrderRepository.save(order));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PurchaseOrderPreviewResponseDTO> getAllPurchaseOrders(
      String text, String key, Pageable pageable) {
    Page<PurchaseOrder> page;
    if (text != null && !text.isBlank()) {
      String effectiveKey = (key != null && !key.isBlank()) ? key.trim().toLowerCase() : "";
      page = purchaseOrderRepository.searchByText(text.trim(), effectiveKey, pageable);
    } else {
      page = purchaseOrderRepository.findAllWithRelations(pageable);
    }
    return page.map(purchaseOrderMapper::toPreviewResponseDTO);
  }

  // ── Lógica de recálculo ──────────────────────────────────────

  void recalculatePurchaseOrderTotals(PurchaseOrder order) {
    List<PurchaseOrderItem> items =
        purchaseOrderItemRepository.findByPurchaseOrderId(order.getId());

    BigDecimal subtotal =
        items.stream()
            .map(i -> i.getUnitPrice().multiply(i.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal ivaTotal =
        items.stream()
            .map(PurchaseOrderItem::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal commDiscountAmount =
        subtotal
            .multiply(
                order.getCommercialDiscountRate() != null
                    ? order.getCommercialDiscountRate()
                    : BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal otherDiscRate =
        order.getOtherDiscountsRate() != null ? order.getOtherDiscountsRate() : BigDecimal.ZERO;
    BigDecimal otherDiscAmount = subtotal.multiply(otherDiscRate).setScale(2, RoundingMode.HALF_UP);

    BigDecimal netBase = subtotal.subtract(commDiscountAmount).subtract(otherDiscAmount);

    // El override de la orden tiene prioridad sobre el tipo configurado en el proveedor.
    Client supplier = order.getSupplier();
    RetefuenteType retefuenteType =
        order.getRetefuenteTypeOverride() != null
            ? order.getRetefuenteTypeOverride()
            : (supplier != null && supplier.getRetefuenteType() != null
                ? supplier.getRetefuenteType()
                : RetefuenteType.NO_APLICA);

    BigDecimal retefuenteRate = null;
    BigDecimal retefuenteAmount = null;
    if (retefuenteType != RetefuenteType.NO_APLICA) {
      retefuenteRate = retefuenteRateFor(retefuenteType);

      BigDecimal retefuenteThreshold =
          systemParamRepository
              .findByClave("RETEFUENTE_THRESHOLD")
              .map(p -> p.getValor())
              .orElse(new BigDecimal("524000"));

      if (netBase.compareTo(retefuenteThreshold) > 0) {
        retefuenteAmount = netBase.multiply(retefuenteRate).setScale(2, RoundingMode.HALF_UP);
      } else {
        retefuenteAmount = BigDecimal.ZERO;
      }
    }

    BigDecimal freight = order.getFreight() != null ? order.getFreight() : BigDecimal.ZERO;

    BigDecimal total =
        netBase
            .add(ivaTotal)
            .subtract(retefuenteAmount != null ? retefuenteAmount : BigDecimal.ZERO)
            .add(freight)
            .setScale(2, RoundingMode.HALF_UP);

    order.setSubtotalAmount(subtotal);
    order.setIvaAmount(ivaTotal);
    order.setCommercialDiscountAmount(commDiscountAmount);
    order.setOtherDiscountsAmount(otherDiscAmount);
    order.setRetefuenteRate(retefuenteRate);
    order.setRetefuenteAmount(retefuenteAmount);
    order.setTotalAmount(total);
  }

  private BigDecimal retefuenteRateFor(RetefuenteType retefuenteType) {
    return switch (retefuenteType) {
      case PERSONA_JURIDICA ->
          systemParamRepository
              .findByClave("RETEFUENTE_RATE_PERSONA_JURIDICA")
              .map(p -> p.getValor())
              .orElse(new BigDecimal("0.0250"));
      case PERSONA_NATURAL ->
          systemParamRepository
              .findByClave("RETEFUENTE_RATE_PERSONA_NATURAL")
              .map(p -> p.getValor())
              .orElse(new BigDecimal("0.0350"));
      case NO_APLICA -> BigDecimal.ZERO;
    };
  }

  private AppUser currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userRepository
        .findByEmailIgnoreCase(auth.getName())
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + auth.getName()));
  }

  private PurchaseOrder findOrder(UUID id) {
    return purchaseOrderRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Purchase order not found with id: " + id));
  }
}
