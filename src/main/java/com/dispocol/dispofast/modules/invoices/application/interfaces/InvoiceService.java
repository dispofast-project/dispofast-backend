package com.dispocol.dispofast.modules.invoices.application.interfaces;

import com.dispocol.dispofast.modules.invoices.api.dtos.InvoiceResponseDTO;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface InvoiceService {

  InvoiceResponseDTO getById(UUID id);

  InvoiceResponseDTO getByOrderId(UUID orderId);

  /**
   * Crea una factura a partir de una orden de venta. Sube el PDF a S3 y persiste la entidad
   * Invoice. Llamado desde SalesOrderService al adjuntar la factura.
   */
  Invoice createFromOrder(SalesOrder order, String invoiceNumber, MultipartFile file);

  /**
   * Crea una factura manual sin orden de venta asociada. Usada por entradas de cartera manuales.
   */
  Invoice createManual(
      String invoiceNumber, UUID clientId, OffsetDateTime issueDate, BigDecimal totalValue);

  byte[] downloadPdf(UUID invoiceId);

  String getPdfFileName(UUID invoiceId);

  /** Descarga el PDF de la factura asociada a una orden. */
  byte[] downloadPdfByOrderId(UUID orderId);

  /** Nombre del archivo PDF de la factura asociada a una orden. */
  String getPdfFileNameByOrderId(UUID orderId);

  Invoice findEntityById(UUID id);
}
