package com.dispocol.dispofast.modules.shipping.application.interfaces;

import com.dispocol.dispofast.modules.shipping.api.dtos.ShipmentHistoryResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.ShipmentResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.UpdateShipmentDTO;
import com.dispocol.dispofast.modules.shipping.domain.Shipment;
import com.dispocol.dispofast.modules.shipping.domain.ShipmentState;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShipmentService {

  /** Lista todos los despachos con filtros opcionales, paginado. */
  Page<ShipmentResponseDTO> getAll(
      ShipmentState state,
      String clientName,
      String asesorName,
      LocalDate dateFrom,
      LocalDate dateTo,
      Pageable pageable);

  ShipmentResponseDTO getById(UUID id);

  ShipmentResponseDTO getByInvoiceId(UUID invoiceId);

  List<ShipmentResponseDTO> getByCarrierId(UUID carrierId);

  /** Crea el despacho automáticamente cuando una orden pasa a estado INVOICED. */
  ShipmentResponseDTO createFromInvoice(UUID invoiceId, String deliveryAddress, String cityCode);

  /** Edita dirección, ciudad y transportista. Solo válido en estado PENDING o ASSIGNED. */
  ShipmentResponseDTO update(UUID id, UpdateShipmentDTO dto);

  /**
   * Cambia el estado del despacho. Cuando el estado es DELAYED (llamado desde el exterior), cancela
   * la orden asociada automáticamente.
   */
  ShipmentResponseDTO updateState(UUID id, ShipmentState state);

  /**
   * Marca el despacho como DELAYED sin propagar la cancelación a la orden. Llamado internamente
   * desde SalesOrderService para evitar ciclos.
   */
  void delayByOrderId(UUID orderId);

  Shipment findEntityById(UUID id);

  /** Retorna el historial de cambios del despacho, más reciente primero. */
  List<ShipmentHistoryResponseDTO> getHistory(UUID id);
}
