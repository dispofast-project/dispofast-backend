package com.dispocol.dispofast.modules.cartera.application.impl;

import com.dispocol.dispofast.modules.cartera.api.dtos.CreatePaymentReceiptRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.mappers.PaymentReceiptMapper;
import com.dispocol.dispofast.modules.cartera.application.interfaces.PaymentReceiptService;
import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import com.dispocol.dispofast.modules.cartera.domain.ArEntryState;
import com.dispocol.dispofast.modules.cartera.domain.PaymentReceipt;
import com.dispocol.dispofast.modules.cartera.infra.persistence.ArEntryRepository;
import com.dispocol.dispofast.modules.cartera.infra.persistence.PaymentReceiptRepository;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import com.dispocol.dispofast.shared.MailService.application.interfaces.MailService;
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
  private static final String NOTIFICATION_EMAIL = "sebasgnv0207@gmail.com";

  private final PaymentReceiptRepository paymentReceiptRepository;
  private final ArEntryRepository arEntryRepository;
  private final UserRepository userRepository;
  private final PaymentReceiptMapper paymentReceiptMapper;
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

    if (arEntry.getState() == ArEntryState.PAID) {
      throw new IllegalStateException("Esta cartera ya se encuentra pagada");
    }

    BigDecimal balance = arEntry.getValue().subtract(arEntry.getPaidAmount());
    if (request.getValue().compareTo(balance) > 0) {
      throw new IllegalArgumentException(
          "El valor del recibo ("
              + request.getValue()
              + ") supera el saldo pendiente ("
              + balance
              + ")");
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AppUser createdBy =
        userRepository
            .findByEmailIgnoreCase(auth.getName())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    PaymentReceipt receipt = new PaymentReceipt();
    receipt.setArEntry(arEntry);
    receipt.setCreatedBy(createdBy);
    receipt.setValue(request.getValue());
    receipt.setPaymentDate(request.getPaymentDate());
    receipt.setPaymentMethod(request.getPaymentMethod());
    receipt.setDocumentNumber(request.getDocumentNumber());
    receipt.setVoucherS3Key(request.getVoucherS3Key());
    receipt.setObservations(request.getObservations());

    paymentReceiptRepository.save(receipt);

    BigDecimal newPaidAmount = arEntry.getPaidAmount().add(request.getValue());
    arEntry.setPaidAmount(newPaidAmount);
    if (newPaidAmount.compareTo(arEntry.getValue()) >= 0) {
      arEntry.setState(ArEntryState.PAID);
    }
    arEntryRepository.save(arEntry);

    sendReceiptEmail(receipt, arEntry);

    return paymentReceiptMapper.toResponseDTO(receipt);
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

  private void sendReceiptEmail(PaymentReceipt receipt, ArEntry arEntry) {
    try {
      BigDecimal thisPayment = receipt.getValue();
      BigDecimal newPaidAmount = arEntry.getPaidAmount();
      BigDecimal previouslyPaid = newPaidAmount.subtract(thisPayment);
      BigDecimal remainingBalance = arEntry.getValue().subtract(newPaidAmount);

      String subject =
          "Recibo de Caja #"
              + receipt.getReceiptCode()
              + " — "
              + arEntry.getClient().getDisplayName();
      String body =
          buildReceiptEmailHtml(receipt, arEntry, thisPayment, previouslyPaid, remainingBalance);

      if (receipt.getVoucherS3Key() != null) {
        byte[] voucherBytes = s3Service.downloadFile(VOUCHERS_BUCKET, receipt.getVoucherS3Key());
        String ext = getExtension(receipt.getVoucherS3Key());
        String filename = "comprobante_" + receipt.getReceiptCode() + ext;
        mailService.sendWithAttchment(
            NOTIFICATION_EMAIL, subject, body, voucherBytes, filename, resolveContentType(ext));
      } else {
        mailService.send(NOTIFICATION_EMAIL, subject, body);
      }
    } catch (Exception e) {
      log.error(
          "Error al enviar correo del recibo {}: {}", receipt.getReceiptCode(), e.getMessage(), e);
    }
  }

  private String buildReceiptEmailHtml(
      PaymentReceipt receipt,
      ArEntry arEntry,
      BigDecimal thisPayment,
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
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
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
