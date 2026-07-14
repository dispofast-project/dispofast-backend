package com.dispocol.dispofast.modules.cartera.application.impl;

import com.dispocol.dispofast.modules.cartera.api.dtos.CreateMultiInvoicePaymentRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreatePaymentReceiptRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.MultiInvoicePaymentResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentAllocationRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.mappers.PaymentReceiptMapper;
import com.dispocol.dispofast.modules.cartera.application.interfaces.PaymentReceiptService;
import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import com.dispocol.dispofast.modules.cartera.domain.ArEntryState;
import com.dispocol.dispofast.modules.cartera.domain.PaymentMethod;
import com.dispocol.dispofast.modules.cartera.domain.PaymentReceipt;
import com.dispocol.dispofast.modules.cartera.infra.persistence.ArEntryRepository;
import com.dispocol.dispofast.modules.cartera.infra.persistence.PaymentReceiptRepository;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderItemRepository;
import com.dispocol.dispofast.shared.MailService.application.interfaces.MailAttachment;
import com.dispocol.dispofast.shared.MailService.application.interfaces.MailService;
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

  private static final String VOUCHERS_BUCKET = "dispofast-payment-vouchers";
  private static final String NOTIFICATION_EMAIL = "cartera@dispocol.com";
  private static final Set<Integer> VALID_PROMPT_PAYMENT_RATES = Set.of(2, 3, 5);

  private final PaymentReceiptRepository paymentReceiptRepository;
  private final ArEntryRepository arEntryRepository;
  private final UserRepository userRepository;
  private final PaymentReceiptMapper paymentReceiptMapper;
  private final SalesOrderItemRepository salesOrderItemRepository;
  private final S3Service s3Service;
  private final MailService mailService;

  @Override
  @Transactional
  public PaymentReceiptResponseDTO createReceipt(
      UUID arEntryId, CreatePaymentReceiptRequestDTO request) {

    ArEntry arEntry =
        arEntryRepository
            .findById(arEntryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Cartera no encontrada: " + arEntryId));

    AppUser createdBy = resolveCurrentUser();

    PaymentReceipt receipt =
        applyReceiptToEntry(
            arEntry,
            request.getValue(),
            request.getPromptPaymentDiscountRate(),
            request.getPaymentDate(),
            request.getPaymentMethod(),
            request.getDocumentNumber(),
            request.getVoucherS3Key(),
            request.getObservations(),
            createdBy,
            null);

    sendReceiptEmail(receipt, arEntry);

    return paymentReceiptMapper.toResponseDTO(receipt);
  }

  @Override
  @Transactional
  public MultiInvoicePaymentResponseDTO createMultiInvoicePayment(
      CreateMultiInvoicePaymentRequestDTO request) {

    List<PaymentAllocationRequestDTO> allocations = request.getAllocations();
    Set<UUID> distinctEntryIds = new HashSet<>();
    for (PaymentAllocationRequestDTO allocation : allocations) {
      if (!distinctEntryIds.add(allocation.getArEntryId())) {
        throw new IllegalArgumentException(
            "No se puede asignar el pago dos veces a la misma factura");
      }
    }

    AppUser createdBy = resolveCurrentUser();
    UUID paymentGroupId = UUID.randomUUID();
    List<PaymentReceipt> receipts = new ArrayList<>();
    List<ArEntry> entries = new ArrayList<>();

    for (PaymentAllocationRequestDTO allocation : allocations) {
      ArEntry arEntry =
          arEntryRepository
              .findById(allocation.getArEntryId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Cartera no encontrada: " + allocation.getArEntryId()));

      if (!arEntry.getClient().getId().equals(request.getClientId())) {
        throw new IllegalArgumentException(
            "Todas las facturas seleccionadas deben pertenecer al mismo cliente");
      }

      PaymentReceipt receipt =
          applyReceiptToEntry(
              arEntry,
              allocation.getValue(),
              allocation.getPromptPaymentDiscountRate(),
              request.getPaymentDate(),
              request.getPaymentMethod(),
              request.getDocumentNumber(),
              allocation.getVoucherS3Key(),
              request.getObservations(),
              createdBy,
              paymentGroupId);

      receipts.add(receipt);
      entries.add(arEntry);
    }

    sendMultiInvoiceReceiptEmail(receipts, entries);

    BigDecimal totalApplied =
        receipts.stream()
            .map(
                r ->
                    r.getValue()
                        .add(
                            r.getPromptPaymentDiscountAmount() != null
                                ? r.getPromptPaymentDiscountAmount()
                                : BigDecimal.ZERO))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    MultiInvoicePaymentResponseDTO response = new MultiInvoicePaymentResponseDTO();
    response.setPaymentGroupId(paymentGroupId);
    response.setReceipts(paymentReceiptMapper.toResponseDTOList(receipts));
    response.setTotalApplied(totalApplied);
    return response;
  }

  /**
   * Valida y aplica un monto (más su descuento por pronto pago, si aplica) al saldo de una
   * factura, creando el recibo correspondiente. Compartido entre el pago de una sola factura y el
   * pago combinado de varias.
   */
  private PaymentReceipt applyReceiptToEntry(
      ArEntry arEntry,
      BigDecimal value,
      Integer discountRate,
      LocalDate paymentDate,
      PaymentMethod paymentMethod,
      String documentNumber,
      String voucherS3Key,
      String observations,
      AppUser createdBy,
      UUID paymentGroupId) {

    if (arEntry.getState() == ArEntryState.PAID) {
      throw new IllegalStateException(
          "La factura "
              + (arEntry.getInvoice() != null ? arEntry.getInvoice().getInvoiceNumber() : arEntry.getId())
              + " ya se encuentra pagada");
    }

    BigDecimal balance = arEntry.getValue().subtract(arEntry.getPaidAmount());

    BigDecimal discountAmount = BigDecimal.ZERO;
    if (discountRate != null) {
      if (!VALID_PROMPT_PAYMENT_RATES.contains(discountRate)) {
        throw new IllegalArgumentException("El descuento por pronto pago debe ser 2%, 3% o 5%");
      }
      discountAmount = calculatePromptPaymentDiscount(arEntry, discountRate);
    }

    BigDecimal settledAmount = value.add(discountAmount);
    if (settledAmount.compareTo(balance) > 0) {
      throw new IllegalArgumentException(
          "El valor asignado a la factura "
              + (arEntry.getInvoice() != null ? arEntry.getInvoice().getInvoiceNumber() : arEntry.getId())
              + " más el descuento por pronto pago ("
              + settledAmount
              + ") supera su saldo pendiente ("
              + balance
              + ")");
    }

    PaymentReceipt receipt = new PaymentReceipt();
    receipt.setArEntry(arEntry);
    receipt.setCreatedBy(createdBy);
    receipt.setValue(value);
    receipt.setPaymentDate(paymentDate);
    receipt.setPaymentMethod(paymentMethod);
    receipt.setDocumentNumber(documentNumber);
    receipt.setVoucherS3Key(voucherS3Key);
    receipt.setObservations(observations);
    receipt.setPromptPaymentDiscountRate(discountRate);
    receipt.setPromptPaymentDiscountAmount(discountRate != null ? discountAmount : null);
    receipt.setPaymentGroupId(paymentGroupId);

    paymentReceiptRepository.save(receipt);

    BigDecimal newPaidAmount = arEntry.getPaidAmount().add(settledAmount);
    arEntry.setPaidAmount(newPaidAmount);
    if (newPaidAmount.compareTo(arEntry.getValue()) >= 0) {
      arEntry.setState(ArEntryState.PAID);
    }
    arEntryRepository.save(arEntry);

    return receipt;
  }

  private AppUser resolveCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userRepository
        .findByEmailIgnoreCase(auth.getName())
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentReceiptResponseDTO> getReceiptsByArEntry(UUID arEntryId) {
    if (!arEntryRepository.existsById(arEntryId)) {
      throw new ResourceNotFoundException("Cartera no encontrada: " + arEntryId);
    }
    return paymentReceiptMapper.toResponseDTOList(
        paymentReceiptRepository.findByArEntryIdOrderByPaymentDateDesc(arEntryId));
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentReceiptResponseDTO getReceiptById(UUID id) {
    return paymentReceiptRepository
        .findById(id)
        .map(paymentReceiptMapper::toResponseDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + id));
  }

  @Override
  public double getTotalPaidValue() {
    return paymentReceiptRepository.sumTotalPaidValue();
  }

  @Override
  public String uploadVoucher(MultipartFile file) {
    String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
    String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
    String key = UUID.randomUUID() + ext;
    try {
      s3Service.uploadFile(
          VOUCHERS_BUCKET,
          key,
          file.getInputStream(),
          file.getContentType() != null ? file.getContentType() : "application/octet-stream",
          file.getSize());
    } catch (IOException e) {
      throw new RuntimeException("Error al subir el comprobante", e);
    }
    return key;
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] downloadVoucher(UUID receiptId) {
    PaymentReceipt receipt =
        paymentReceiptRepository
            .findById(receiptId)
            .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + receiptId));
    if (receipt.getVoucherS3Key() == null) {
      throw new ResourceNotFoundException("Este recibo no tiene comprobante adjunto");
    }
    return s3Service.downloadFile(VOUCHERS_BUCKET, receipt.getVoucherS3Key());
  }

  @Override
  @Transactional(readOnly = true)
  public String getVoucherFilename(UUID receiptId) {
    PaymentReceipt receipt =
        paymentReceiptRepository
            .findById(receiptId)
            .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + receiptId));
    String key = receipt.getVoucherS3Key();
    String ext = (key != null && key.contains(".")) ? key.substring(key.lastIndexOf('.')) : "";
    String docRef =
        receipt.getDocumentNumber() != null
            ? receipt.getDocumentNumber()
            : receipt.getReceiptCode();
    return "comprobante_" + docRef + ext;
  }

  /**
   * Calcula el descuento por pronto pago como un porcentaje del subtotal antes de impuestos de la
   * orden asociada (suma de los line totals de sus ítems, antes de IVA).
   */
  private BigDecimal calculatePromptPaymentDiscount(ArEntry arEntry, int discountRate) {
    SalesOrder order = arEntry.getOrder();
    if (order == null) {
      throw new IllegalArgumentException(
          "Esta cartera no tiene una orden asociada; no se puede aplicar descuento por pronto"
              + " pago");
    }
    BigDecimal subtotal =
        salesOrderItemRepository.findByOrderId(order.getId()).stream()
            .map(item -> item.getLineTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return subtotal
        .multiply(BigDecimal.valueOf(discountRate))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }

  private void sendReceiptEmail(PaymentReceipt receipt, ArEntry arEntry) {
    try {
      BigDecimal thisPayment = receipt.getValue();
      BigDecimal discountAmount =
          receipt.getPromptPaymentDiscountAmount() != null
              ? receipt.getPromptPaymentDiscountAmount()
              : BigDecimal.ZERO;
      BigDecimal newPaidAmount = arEntry.getPaidAmount();
      BigDecimal previouslyPaid = newPaidAmount.subtract(thisPayment).subtract(discountAmount);
      BigDecimal remainingBalance = arEntry.getValue().subtract(newPaidAmount);

      String subject =
          "Recibo de Caja #"
              + receipt.getReceiptCode()
              + " — "
              + arEntry.getClient().getDisplayName();
      String body =
          buildReceiptEmailHtml(
              receipt, arEntry, thisPayment, discountAmount, previouslyPaid, remainingBalance);

      if (receipt.getVoucherS3Key() != null) {
        byte[] voucherBytes = s3Service.downloadFile(VOUCHERS_BUCKET, receipt.getVoucherS3Key());
        String ext = getExtension(receipt.getVoucherS3Key());
        String filename = "comprobante_" + receipt.getReceiptCode() + ext;
        mailService.sendWithAttchment(
            new String[] {NOTIFICATION_EMAIL},
            subject,
            body,
            voucherBytes,
            filename,
            resolveContentType(ext));
      } else {
        mailService.send(new String[] {NOTIFICATION_EMAIL}, subject, body);
      }
    } catch (Exception e) {
      log.error(
          "Error al enviar correo del recibo {}: {}", receipt.getReceiptCode(), e.getMessage(), e);
    }
  }

  private void sendMultiInvoiceReceiptEmail(List<PaymentReceipt> receipts, List<ArEntry> entries) {
    try {
      Client client = entries.get(0).getClient();
      String groupCode = receipts.get(0).getPaymentGroupId().toString().replace("-", "").substring(0, 13);

      String subject = "Pago combinado #" + groupCode + " — " + client.getDisplayName();
      String body = buildMultiInvoiceEmailHtml(receipts, entries, client, groupCode);

      List<MailAttachment> attachments = new ArrayList<>();
      for (PaymentReceipt receipt : receipts) {
        String voucherKey = receipt.getVoucherS3Key();
        if (voucherKey != null) {
          byte[] voucherBytes = s3Service.downloadFile(VOUCHERS_BUCKET, voucherKey);
          String ext = getExtension(voucherKey);
          attachments.add(
              new MailAttachment(
                  voucherBytes, "comprobante_" + receipt.getReceiptCode() + ext, resolveContentType(ext)));
        }
      }

      if (!attachments.isEmpty()) {
        mailService.sendWithAttachments(new String[] {NOTIFICATION_EMAIL}, subject, body, attachments);
      } else {
        mailService.send(new String[] {NOTIFICATION_EMAIL}, subject, body);
      }
    } catch (Exception e) {
      log.error("Error al enviar correo del pago combinado: {}", e.getMessage(), e);
    }
  }

  private String buildMultiInvoiceEmailHtml(
      List<PaymentReceipt> receipts, List<ArEntry> entries, Client client, String groupCode) {

    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    PaymentReceipt first = receipts.get(0);

    BigDecimal totalCash =
        receipts.stream().map(PaymentReceipt::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalDiscount =
        receipts.stream()
            .map(r -> r.getPromptPaymentDiscountAmount() != null ? r.getPromptPaymentDiscountAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    StringBuilder rows = new StringBuilder();
    for (int i = 0; i < receipts.size(); i++) {
      PaymentReceipt receipt = receipts.get(i);
      ArEntry entry = entries.get(i);
      BigDecimal remaining = entry.getValue().subtract(entry.getPaidAmount());
      boolean paid = remaining.compareTo(BigDecimal.ZERO) <= 0;
      String invoiceNumber =
          entry.getInvoice() != null ? esc(entry.getInvoice().getInvoiceNumber()) : "N/A";
      BigDecimal discount =
          receipt.getPromptPaymentDiscountAmount() != null
              ? receipt.getPromptPaymentDiscountAmount()
              : BigDecimal.ZERO;
      String appliedLabel =
          discount.compareTo(BigDecimal.ZERO) > 0
              ? fmt(receipt.getValue()) + " + " + fmt(discount) + " (pronto pago "
                  + receipt.getPromptPaymentDiscountRate() + "%)"
              : fmt(receipt.getValue());

      rows.append("<tr><td style=\"padding:6px 0;border-bottom:1px solid #eee;color:#222;\">")
          .append(invoiceNumber)
          .append("</td><td style=\"padding:6px 0;border-bottom:1px solid #eee;color:#666;text-align:right;\">")
          .append(appliedLabel)
          .append("</td><td style=\"padding:6px 0;border-bottom:1px solid #eee;text-align:right;font-weight:bold;color:")
          .append(paid ? "#2e7d32" : "#1a3c5e")
          .append("\">")
          .append(paid ? "PAGADA" : fmt(remaining))
          .append("</td></tr>");
    }

    String paymentMethodDisplay =
        first.getPaymentMethod().name().equalsIgnoreCase("TRANSFERENCIA")
            ? "Transferencia Bancaria"
            : "Caja";

    String docNumberRow =
        first.getDocumentNumber() != null
            ? "<tr><td style=\"padding:5px 0;color:#666;\">N&deg; Documento</td>"
                + "<td style=\"padding:5px 0;color:#222;\">"
                + esc(first.getDocumentNumber())
                + "</td></tr>"
            : "";

    return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"/></head>"
        + "<body style=\"margin:0;padding:0;background-color:#f0f2f5;font-family:Arial,Helvetica,sans-serif;\">"
        + "<div style=\"max-width:640px;margin:30px auto;background:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);\">"
        + "<div style=\"background-color:#1a3c5e;padding:24px 30px;text-align:center;\">"
        + "<h1 style=\"color:#ffffff;margin:0;font-size:22px;letter-spacing:1px;\">PAGO COMBINADO</h1>"
        + "<p style=\"color:#7eb8e8;margin:6px 0 0;font-size:15px;font-weight:bold;\"># "
        + groupCode
        + "</p></div>"
        + "<div style=\"padding:22px 30px;background-color:#f7f9fc;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:#1a3c5e;margin:0 0 14px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Informaci&oacute;n del Cliente</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
        + "<tr><td style=\"padding:5px 0;color:#666;width:40%;\">Cliente</td>"
        + "<td style=\"padding:5px 0;color:#222;font-weight:bold;\">"
        + esc(client.getDisplayName())
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Identificaci&oacute;n / NIT</td>"
        + "<td style=\"padding:5px 0;color:#222;\">"
        + esc(client.getIdentificationNumber())
        + "</td></tr>"
        + "</table></div>"
        + "<div style=\"padding:22px 30px;background-color:#f7f9fc;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:#1a3c5e;margin:0 0 14px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Detalle del Pago</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
        + "<tr><td style=\"padding:5px 0;color:#666;width:40%;\">Fecha de Pago</td><td style=\"padding:5px 0;color:#222;\">"
        + first.getPaymentDate().format(dateFmt)
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Forma de Pago</td><td style=\"padding:5px 0;color:#222;\">"
        + paymentMethodDisplay
        + "</td></tr>"
        + docNumberRow
        + "<tr><td style=\"padding:5px 0;color:#666;\">Efectivo Recibido</td><td style=\"padding:5px 0;color:#1a3c5e;font-weight:bold;\">"
        + fmt(totalCash)
        + "</td></tr>"
        + (totalDiscount.compareTo(BigDecimal.ZERO) > 0
            ? "<tr><td style=\"padding:5px 0;color:#666;\">Descuento Pronto Pago</td><td style=\"padding:5px 0;color:#2e7d32;font-weight:bold;\">- "
                + fmt(totalDiscount)
                + "</td></tr>"
            : "")
        + "</table></div>"
        + "<div style=\"padding:22px 30px;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:#1a3c5e;margin:0 0 14px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Facturas Cubiertas</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:13px;\">"
        + "<tr><th style=\"text-align:left;color:#666;padding-bottom:6px;border-bottom:2px solid #d0d5dd;\">Factura</th>"
        + "<th style=\"text-align:right;color:#666;padding-bottom:6px;border-bottom:2px solid #d0d5dd;\">Aplicado</th>"
        + "<th style=\"text-align:right;color:#666;padding-bottom:6px;border-bottom:2px solid #d0d5dd;\">Saldo Resultante</th></tr>"
        + rows
        + "</table></div>"
        + "<div style=\"padding:16px 30px;text-align:center;font-size:12px;color:#999;border-top:1px solid #e1e6ed;\">"
        + "<p style=\"margin:0;\">Registrado por: <strong>"
        + esc(first.getCreatedBy().getFullName())
        + "</strong></p>"
        + "<p style=\"margin:8px 0 0;\">Dispofast &mdash; Sistema de Gesti&oacute;n Log&iacute;stica</p>"
        + "</div>"
        + "</div></body></html>";
  }

  private String buildReceiptEmailHtml(
      PaymentReceipt receipt,
      ArEntry arEntry,
      BigDecimal thisPayment,
      BigDecimal discountAmount,
      BigDecimal previouslyPaid,
      BigDecimal remainingBalance) {

    Client client = arEntry.getClient();
    Invoice invoice = arEntry.getInvoice();
    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    String invoiceNumber = invoice != null ? esc(invoice.getInvoiceNumber()) : "N/A";
    String invoiceDate =
        invoice != null ? invoice.getIssueDate().toLocalDate().format(dateFmt) : "N/A";

    boolean paid = remainingBalance.compareTo(BigDecimal.ZERO) <= 0;
    String balanceColor = paid ? "#2e7d32" : "#1a3c5e";
    String balanceBg = paid ? "#e8f5e9" : "#eef2fc";
    String balanceText = paid ? "PAGADA" : fmt(remainingBalance);

    String docNumberRow =
        receipt.getDocumentNumber() != null
            ? "<tr><td style=\"padding:5px 0;color:#666;\">N&deg; Documento</td>"
                + "<td style=\"padding:5px 0;color:#222;\">"
                + esc(receipt.getDocumentNumber())
                + "</td></tr>"
            : "";

    String discountRow =
        receipt.getPromptPaymentDiscountRate() != null
            ? "<tr><td style=\"padding:5px 0;color:#666;\">Descuento Pronto Pago ("
                + receipt.getPromptPaymentDiscountRate()
                + "%)</td>"
                + "<td style=\"padding:5px 0;color:#2e7d32;font-weight:bold;\">- "
                + fmt(discountAmount)
                + "</td></tr>"
            : "";

    String discountSummaryRow =
        discountAmount.compareTo(BigDecimal.ZERO) > 0
            ? "<tr><td style=\"padding:6px 0;color:#666;\">Descuento Pronto Pago</td>"
                + "<td style=\"padding:6px 0;text-align:right;color:#2e7d32;\">- "
                + fmt(discountAmount)
                + "</td></tr>"
            : "";

    String observationsSection = "";
    if (receipt.getObservations() != null && !receipt.getObservations().isBlank()) {
      observationsSection =
          "<div style=\"padding:22px 30px;background-color:#fffbe6;border-bottom:1px solid #e1e6ed;\">"
              + "<h2 style=\"color:#7a5f00;margin:0 0 10px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Observaciones</h2>"
              + "<p style=\"margin:0;color:#555;font-size:14px;line-height:1.5;\">"
              + esc(receipt.getObservations())
              + "</p></div>";
    }

    String paymentMethodDisplay =
        receipt.getPaymentMethod().name().equalsIgnoreCase("TRANSFERENCIA")
            ? "Transferencia Bancaria"
            : "Caja";

    String createdAt =
        receipt.getCreatedAt() != null
            ? receipt.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            : "";

    return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"/></head>"
        + "<body style=\"margin:0;padding:0;background-color:#f0f2f5;font-family:Arial,Helvetica,sans-serif;\">"
        + "<div style=\"max-width:620px;margin:30px auto;background:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);\">"
        // Header
        + "<div style=\"background-color:#1a3c5e;padding:24px 30px;text-align:center;\">"
        + "<h1 style=\"color:#ffffff;margin:0;font-size:22px;letter-spacing:1px;\">RECIBO DE CAJA</h1>"
        + "<p style=\"color:#7eb8e8;margin:6px 0 0;font-size:15px;font-weight:bold;\"># "
        + receipt.getReceiptCode()
        + "</p></div>"
        // Client info
        + "<div style=\"padding:22px 30px;background-color:#f7f9fc;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:#1a3c5e;margin:0 0 14px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Informaci&oacute;n del Cliente</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
        + "<tr><td style=\"padding:5px 0;color:#666;width:40%;\">Cliente</td>"
        + "<td style=\"padding:5px 0;color:#222;font-weight:bold;\">"
        + esc(client.getDisplayName())
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Correo</td>"
        + "<td style=\"padding:5px 0;color:#222;\">"
        + esc(client.getEmail())
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Identificaci&oacute;n / NIT</td>"
        + "<td style=\"padding:5px 0;color:#222;\">"
        + esc(client.getIdentificationNumber())
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Tel&eacute;fono</td>"
        + "<td style=\"padding:5px 0;color:#222;\">"
        + esc(client.getPhone())
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Direcci&oacute;n</td>"
        + "<td style=\"padding:5px 0;color:#222;\">"
        + esc(client.getAddress())
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Asesor</td>"
        + "<td style=\"padding:5px 0;color:#222;\">"
        + esc(client.getDefaultAdvisor() != null ? client.getDefaultAdvisor().getFullName() : "N/A")
        + "</td></tr>"
        + "</table></div>"
        // Invoice detail
        + "<div style=\"padding:22px 30px;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:#1a3c5e;margin:0 0 14px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Detalle de la Factura</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
        + "<tr><td style=\"padding:5px 0;color:#666;width:40%;\">N&deg; Factura</td><td style=\"padding:5px 0;color:#222;\">"
        + invoiceNumber
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Fecha Factura</td><td style=\"padding:5px 0;color:#222;\">"
        + invoiceDate
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Valor Total</td><td style=\"padding:5px 0;color:#222;\">"
        + fmt(arEntry.getValue())
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Plazo</td><td style=\"padding:5px 0;color:#222;\">"
        + arEntry.getPaymentTermDays()
        + " d&iacute;as</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Vencimiento</td><td style=\"padding:5px 0;color:#222;\">"
        + arEntry.getExpirationDate().toLocalDate().format(dateFmt)
        + "</td></tr>"
        + "</table></div>"
        // Payment detail
        + "<div style=\"padding:22px 30px;background-color:#f7f9fc;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:#1a3c5e;margin:0 0 14px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Detalle del Pago</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
        + "<tr><td style=\"padding:5px 0;color:#666;width:40%;\">C&oacute;digo Recibo</td>"
        + "<td style=\"padding:5px 0;color:#222;font-family:monospace;\">"
        + receipt.getReceiptCode()
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Fecha de Pago</td><td style=\"padding:5px 0;color:#222;\">"
        + receipt.getPaymentDate().format(dateFmt)
        + "</td></tr>"
        + "<tr><td style=\"padding:5px 0;color:#666;\">Forma de Pago</td><td style=\"padding:5px 0;color:#222;\">"
        + paymentMethodDisplay
        + "</td></tr>"
        + docNumberRow
        + "<tr><td style=\"padding:5px 0;color:#666;\">Valor Pagado</td>"
        + "<td style=\"padding:5px 0;color:#1a3c5e;font-weight:bold;font-size:15px;\">"
        + fmt(thisPayment)
        + "</td></tr>"
        + discountRow
        + "</table></div>"
        // Financial summary
        + "<div style=\"padding:22px 30px;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:#1a3c5e;margin:0 0 14px;font-size:14px;text-transform:uppercase;letter-spacing:0.5px;\">Resumen Financiero</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
        + "<tr><td style=\"padding:6px 0;color:#666;\">Valor Total Factura</td>"
        + "<td style=\"padding:6px 0;text-align:right;color:#222;\">"
        + fmt(arEntry.getValue())
        + "</td></tr>"
        + "<tr><td style=\"padding:6px 0;color:#666;\">Pagos Anteriores</td>"
        + "<td style=\"padding:6px 0;text-align:right;color:#666;\">- "
        + fmt(previouslyPaid)
        + "</td></tr>"
        + "<tr style=\"border-top:1px solid #d0d5dd;\">"
        + "<td style=\"padding:8px 0;color:#1a3c5e;font-weight:bold;\">Este Pago</td>"
        + "<td style=\"padding:8px 0;text-align:right;color:#1a3c5e;font-weight:bold;\">- "
        + fmt(thisPayment)
        + "</td></tr>"
        + discountSummaryRow
        + "<tr><td style=\"padding:8px 6px;background-color:"
        + balanceBg
        + ";color:"
        + balanceColor
        + ";font-weight:bold;\">Saldo Pendiente</td>"
        + "<td style=\"padding:8px 6px;background-color:"
        + balanceBg
        + ";text-align:right;color:"
        + balanceColor
        + ";font-weight:bold;\">"
        + balanceText
        + "</td></tr>"
        + "</table></div>"
        + observationsSection
        // Footer
        + "<div style=\"padding:16px 30px;text-align:center;font-size:12px;color:#999;border-top:1px solid #e1e6ed;\">"
        + "<p style=\"margin:0;\">Registrado por: <strong>"
        + esc(receipt.getCreatedBy().getFullName())
        + "</strong> &nbsp;|&nbsp; "
        + createdAt
        + "</p>"
        + "<p style=\"margin:8px 0 0;\">Dispofast &mdash; Sistema de Gesti&oacute;n Log&iacute;stica</p>"
        + "</div>"
        + "</div></body></html>";
  }

  private static String fmt(BigDecimal value) {
    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "CO"));
    nf.setMinimumFractionDigits(0);
    nf.setMaximumFractionDigits(0);
    return "$ " + nf.format(value);
  }

  private static String esc(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;");
  }

  private static String resolveContentType(String extension) {
    return switch (extension.toLowerCase()) {
      case ".pdf" -> "application/pdf";
      case ".png" -> "image/png";
      case ".jpg", ".jpeg" -> "image/jpeg";
      case ".gif" -> "image/gif";
      case ".tiff", ".tif" -> "image/tiff";
      default -> "application/octet-stream";
    };
  }

  private static String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) return "";
    return filename.substring(filename.lastIndexOf('.'));
  }
}
