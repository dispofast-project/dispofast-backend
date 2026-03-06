package com.dispocol.dispofast.modules.orders.application.interfaces;

import com.dispocol.dispofast.modules.orders.api.dtos.AttachInvoiceRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.CreateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderFilterDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderResponseDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.UpdateSalesOrderRequestDTO;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesOrderService {

  SalesOrderResponseDTO createSalesOrder(CreateSalesOrderRequestDTO request);

  SalesOrderResponseDTO createSalesOrderFromQuote(UUID quoteId);

  SalesOrderResponseDTO getSalesOrderById(UUID id);

  Page<SalesOrderResponseDTO> getAllSalesOrders(Pageable pageable, SalesOrderFilterDTO filter);

  SalesOrderResponseDTO updateSalesOrder(UUID id, UpdateSalesOrderRequestDTO request);

  SalesOrderResponseDTO attachInvoice(UUID id, AttachInvoiceRequestDTO request);

  void deleteSalesOrder(UUID id);
}
