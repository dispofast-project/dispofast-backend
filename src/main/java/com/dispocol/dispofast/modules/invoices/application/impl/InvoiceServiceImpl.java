package com.dispocol.dispofast.modules.invoices.application.impl;

import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.invoices.api.dtos.InvoiceResponseDTO;
import com.dispocol.dispofast.modules.invoices.api.mappers.InvoiceMapper;
import com.dispocol.dispofast.modules.invoices.application.interfaces.InvoiceService;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import com.dispocol.dispofast.modules.invoices.domain.InvoiceState;
import com.dispocol.dispofast.modules.invoices.infra.exceptions.InvoiceNotFoundException;
import com.dispocol.dispofast.modules.invoices.infra.persistence.InvoiceRepository;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

  private static final String INVOICE_BUCKET = "dispofast-invoices";

  private final InvoiceRepository invoiceRepository;
  private final ClientRepository clientRepository;
  private final S3Service s3Service;
  private final InvoiceMapper invoiceMapper;

  @Override
  @Transactional(readOnly = true)
  public InvoiceResponseDTO getById(UUID id) {
    return invoiceMapper.toResponseDTO(findEntityById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public InvoiceResponseDTO getByOrderId(UUID orderId) {
    Invoice invoice =
        invoiceRepository
            .findBySalesOrderId(orderId)
            .orElseThrow(
                () ->
                    new InvoiceNotFoundException(
                        "No se encontró factura para la orden: " + orderId));
    return invoiceMapper.toResponseDTO(invoice);
  }

  @Override
  @Transactional
  public Invoice createFromOrder(SalesOrder order, String invoiceNumber, MultipartFile file) {
    String fileKey = order.getId() + "/" + file.getOriginalFilename();
    try {
      s3Service.uploadFile(
          INVOICE_BUCKET,
          fileKey,
          file.getInputStream(),
          file.getContentType(),
          file.getSize());
    } catch (IOException e) {
      throw new IllegalArgumentException("Error al guardar la factura: " + e.getMessage(), e);
    }

    Invoice invoice = new Invoice();
    invoice.setSalesOrder(order);
    invoice.setInvoiceNumber(invoiceNumber);
    invoice.setClient(order.getClient());
    invoice.setIssueDate(OffsetDateTime.now());
    invoice.setTotalValue(
        order.getTotalValue() != null ? order.getTotalValue() : BigDecimal.ZERO);
    invoice.setPdfS3Key(fileKey);
    invoice.setState(InvoiceState.ISSUED);
    return invoiceRepository.save(invoice);
  }

  @Override
  @Transactional
  public Invoice createManual(
      String invoiceNumber, UUID clientId, OffsetDateTime issueDate, BigDecimal totalValue) {
    Invoice invoice = new Invoice();
    invoice.setInvoiceNumber(invoiceNumber);
    invoice.setClient(clientRepository.getReferenceById(clientId));
    invoice.setIssueDate(issueDate);
    invoice.setTotalValue(totalValue);
    invoice.setState(InvoiceState.ISSUED);
    return invoiceRepository.save(invoice);
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] downloadPdf(UUID invoiceId) {
    Invoice invoice = findEntityById(invoiceId);
    if (invoice.getPdfS3Key() == null) {
      throw new IllegalStateException("La factura no tiene un PDF adjunto: " + invoiceId);
    }
    return s3Service.downloadFile(INVOICE_BUCKET, invoice.getPdfS3Key());
  }

  @Override
  @Transactional(readOnly = true)
  public String getPdfFileName(UUID invoiceId) {
    Invoice invoice = findEntityById(invoiceId);
    return extractFileName(invoice.getPdfS3Key());
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] downloadPdfByOrderId(UUID orderId) {
    Invoice invoice =
        invoiceRepository
            .findBySalesOrderId(orderId)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "La orden no tiene una factura adjunta: " + orderId));
    if (invoice.getPdfS3Key() == null) {
      throw new IllegalStateException("La factura no tiene un PDF adjunto: " + invoice.getId());
    }
    return s3Service.downloadFile(INVOICE_BUCKET, invoice.getPdfS3Key());
  }

  @Override
  @Transactional(readOnly = true)
  public String getPdfFileNameByOrderId(UUID orderId) {
    Invoice invoice =
        invoiceRepository.findBySalesOrderId(orderId).orElse(null);
    if (invoice == null) {
      return "factura.pdf";
    }
    return extractFileName(invoice.getPdfS3Key());
  }

  @Override
  @Transactional(readOnly = true)
  public Invoice findEntityById(UUID id) {
    return invoiceRepository
        .findById(id)
        .orElseThrow(
            () -> new InvoiceNotFoundException("Factura no encontrada con id: " + id));
  }

  private String extractFileName(String s3Key) {
    if (s3Key == null) return "factura.pdf";
    return s3Key.contains("/") ? s3Key.substring(s3Key.lastIndexOf('/') + 1) : s3Key;
  }
}
