package com.dispocol.dispofast.modules.cartera.application.interfaces;

import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryFilterDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CarteraStatsDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreateManualArEntryRequestDTO;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArEntryService {

  /**
   * Retorna entradas de cartera paginadas con filtros opcionales. Si el usuario autenticado tiene
   * rol VENDEDOR, solo ve la cartera de sus clientes asignados.
   */
  Page<ArEntryResponseDTO> getArEntries(Pageable pageable, ArEntryFilterDTO filter);

  /**
   * Totales de cartera (pendiente y vencida) calculados en base de datos, respetando la misma
   * visibilidad por rol que {@link #getArEntries}.
   */
  CarteraStatsDTO getStats();

  /** Nombres de asesores con entradas de cartera, para poblar el filtro de la UI. */
  List<String> getAsesorNames();

  /**
   * Crea un registro de cartera manual. Solo disponible para ADMIN. Crea internamente un Invoice
   * sin orden asociada.
   */
  ArEntryResponseDTO createManualEntry(CreateManualArEntryRequestDTO request);

  /**
   * Crea automáticamente un registro de cartera cuando una orden pasa a estado INVOICED. Llamado
   * internamente desde SalesOrderService con la Invoice ya persistida.
   */
  ArEntryResponseDTO createFromOrder(Invoice invoice);
}
