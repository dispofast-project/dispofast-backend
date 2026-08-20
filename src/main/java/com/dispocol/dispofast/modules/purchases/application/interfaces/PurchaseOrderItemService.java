package com.dispocol.dispofast.modules.purchases.application.interfaces;

import com.dispocol.dispofast.modules.purchases.api.dtos.AddPurchaseOrderItemRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderItemResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.UpdatePurchaseOrderItemRequestDTO;
import java.util.List;
import java.util.UUID;

public interface PurchaseOrderItemService {

  PurchaseOrderItemResponseDTO addItem(UUID purchaseOrderId, AddPurchaseOrderItemRequestDTO dto);

  PurchaseOrderItemResponseDTO updateItem(
      UUID purchaseOrderId, UUID itemId, UpdatePurchaseOrderItemRequestDTO dto);

  void removeItem(UUID purchaseOrderId, UUID itemId);

  List<PurchaseOrderItemResponseDTO> getItems(UUID purchaseOrderId);
}
