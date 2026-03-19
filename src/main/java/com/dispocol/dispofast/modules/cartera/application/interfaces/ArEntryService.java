package com.dispocol.dispofast.modules.cartera.application.interfaces;

import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryFilterDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreateManualArEntryRequestDTO;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArEntryService {

  /**
   * Retorna entradas de cartera paginadas con filtros opcionales. Si el usuario autenticado tiene
   * rol VENDEDOR, solo ve la cartera de sus clientes asignados.
   */
  Page<ArEntryResponseDTO> getArEntries(Pageable pageable, ArEntryFilterDTO filter);

  /**
   * Crea un registro de cartera manual (Osteosíntesis). Solo disponible para ADMIN. No requiere
   * order_id.
   */
  ArEntryResponseDTO createManualEntry(CreateManualArEntryRequestDTO request);

  /**
   * Crea automáticamente un registro de cartera cuando una orden pasa a estado INVOICED. Llamado
   * internamente desde SalesOrderService.
   */
  ArEntryResponseDTO createFromOrder(SalesOrder order);
}
