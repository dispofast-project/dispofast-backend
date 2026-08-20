package com.dispocol.dispofast.modules.purchases.application.interfaces;

import com.dispocol.dispofast.modules.purchases.api.dtos.CreatePurchaseOrderRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderPreviewResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.UpdatePurchaseOrderRequestDTO;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderService {

  PurchaseOrderResponseDTO createPurchaseOrder(CreatePurchaseOrderRequestDTO dto);

  PurchaseOrderResponseDTO getPurchaseOrderById(UUID id);

  PurchaseOrderResponseDTO updatePurchaseOrder(UUID id, UpdatePurchaseOrderRequestDTO dto);

  Page<PurchaseOrderPreviewResponseDTO> getAllPurchaseOrders(
      String text, String key, Pageable pageable);
}
