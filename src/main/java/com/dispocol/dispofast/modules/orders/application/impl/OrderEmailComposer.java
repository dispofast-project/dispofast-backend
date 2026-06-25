package com.dispocol.dispofast.modules.orders.application.impl;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderItemResponseDTO;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import com.dispocol.dispofast.shared.MailService.application.interfaces.MailService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEmailComposer {

  private static final String NOTIFICATION_EMAIL = "sebasgnv0207@gmail.com";
  private static final BigDecimal IVA_RATE = BigDecimal.valueOf(0.19);
  private static final String PRIMARY = "#4676B8";
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private static final Map<String, String> PAYMENT_LABELS =
      Map.of(
          "CONTADO", "Contado",
          "CREDITO_15_DIAS", "Crédito 15 días",
          "CREDITO_30_DIAS", "Crédito 30 días",
          "CREDITO_60_DIAS", "Crédito 60 días",
          "CREDITO_90_DIAS", "Crédito 90 días",
          "CONTRAENTREGA", "Contra entrega");

  private final MailService mailService;

  public void sendOrderCreatedEmail(SalesOrder order, List<SalesOrderItemResponseDTO> items) {
    try {
      BigDecimal subtotal =
          items.stream()
              .map(SalesOrderItemResponseDTO::getLineTotal)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal discountAmt = BigDecimal.ZERO;
      if (order.getDiscountRate() != null && order.getDiscountRate() > 0) {
        discountAmt =
            subtotal
                .multiply(BigDecimal.valueOf(order.getDiscountRate()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      }

      BigDecimal additionalDiscountAmt = BigDecimal.ZERO;
      if (order.getAdditionalDiscountRate() != null
          && order.getAdditionalDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
        additionalDiscountAmt =
            subtotal
                .multiply(order.getAdditionalDiscountRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      }

      BigDecimal tax = order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO;
      BigDecimal retefuente =
          order.getRetefuenteAmount() != null ? order.getRetefuenteAmount() : BigDecimal.ZERO;
      BigDecimal freight = order.getFreight() != null ? order.getFreight() : BigDecimal.ZERO;
      BigDecimal total = order.getTotalValue() != null ? order.getTotalValue() : BigDecimal.ZERO;

      String subject =
          "Orden de Compra #" + order.getOrderNumber() + " — " + order.getClient().getDisplayName();

      String body =
          buildEmailHtml(
              order,
              items,
              subtotal,
              tax,
              discountAmt,
              additionalDiscountAmt,
              retefuente,
              freight,
              total);

      mailService.send(NOTIFICATION_EMAIL, subject, body);

    } catch (Exception e) {
      log.error(
          "Error al enviar correo de la orden {}: {}", order.getOrderNumber(), e.getMessage(), e);
    }
  }

  private String buildEmailHtml(
      SalesOrder order,
      List<SalesOrderItemResponseDTO> items,
      BigDecimal subtotal,
      BigDecimal tax,
      BigDecimal discountAmt,
      BigDecimal additionalDiscountAmt,
      BigDecimal retefuente,
      BigDecimal freight,
      BigDecimal total) {

    Client client = order.getClient();
    String clientName = esc(client.getDisplayName());
    String nit = esc(client.getIdentificationNumber());
    String clientEmail = esc(client.getEmail());
    String phone = esc(client.getPhone());
    String address = esc(client.getAddress());

    String dateLabel =
        order.getOrderDate() != null ? order.getOrderDate().toLocalDate().format(DATE_FMT) : "-";

    String paymentLabel =
        order.getPaymentCondition() != null
            ? PAYMENT_LABELS.getOrDefault(
                order.getPaymentCondition().name(), order.getPaymentCondition().name())
            : "-";

    String shipmentCity =
        order.getShipmentCity() != null ? esc(order.getShipmentCity().getName()) : "-";
    String shipmentAddress = esc(order.getShipmentAddress());
    String asesorName = order.getAsesor() != null ? esc(order.getAsesor().getFullName()) : "-";

    String discountStr =
        order.getDiscountRate() != null && order.getDiscountRate() > 0
            ? order.getDiscountRate() + "%"
            : "No aplica";
    String addDiscountStr =
        order.getAdditionalDiscountRate() != null
                && order.getAdditionalDiscountRate().compareTo(BigDecimal.ZERO) > 0
            ? order.getAdditionalDiscountRate().stripTrailingZeros().toPlainString() + "%"
            : "No aplica";

    // Product rows
    StringBuilder itemRows = new StringBuilder();
    for (int i = 0; i < items.size(); i++) {
      SalesOrderItemResponseDTO item = items.get(i);
      BigDecimal ivaAmt =
          item.isTaxFree()
              ? BigDecimal.ZERO
              : item.getLineTotal().multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
      BigDecimal rowTotal = item.getLineTotal().add(ivaAmt);
      String rowBg = i % 2 == 0 ? "#ffffff" : "#f5f7fa";
      itemRows
          .append("<tr style=\"background-color:")
          .append(rowBg)
          .append(";\">")
          .append(
              "<td style=\"padding:7px 10px;border-bottom:1px solid #e8e8e8;font-size:12px;color:#555;\">")
          .append(esc(item.getProductReference()))
          .append("</td>")
          .append("<td style=\"padding:7px 10px;border-bottom:1px solid #e8e8e8;font-size:12px;\">")
          .append(esc(item.getProductName()))
          .append("</td>")
          .append(
              "<td style=\"padding:7px 10px;border-bottom:1px solid #e8e8e8;font-size:12px;text-align:center;\">")
          .append(item.getQuantity().stripTrailingZeros().toPlainString())
          .append("</td>")
          .append(
              "<td style=\"padding:7px 10px;border-bottom:1px solid #e8e8e8;font-size:12px;text-align:right;\">")
          .append(fmt(item.getLineTotal()))
          .append("</td>")
          .append(
              "<td style=\"padding:7px 10px;border-bottom:1px solid #e8e8e8;font-size:12px;text-align:right;\">")
          .append(fmt(ivaAmt))
          .append("</td>")
          .append(
              "<td style=\"padding:7px 10px;border-bottom:1px solid #e8e8e8;font-size:12px;text-align:right;font-weight:bold;\">")
          .append(fmt(rowTotal))
          .append("</td>")
          .append("</tr>");
    }

    String observationsSection =
        order.getObservations() != null && !order.getObservations().isBlank()
            ? "<div style=\"padding:18px 30px;background-color:#fffbe6;border-bottom:1px solid #e1e6ed;\">"
                + "<h2 style=\"color:#7a5f00;margin:0 0 8px;font-size:13px;text-transform:uppercase;\">Observaciones</h2>"
                + "<p style=\"margin:0;color:#555;font-size:13px;line-height:1.5;\">"
                + esc(order.getObservations())
                + "</p></div>"
            : "";

    return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"/></head>"
        + "<body style=\"margin:0;padding:0;background-color:#f0f2f5;font-family:Arial,Helvetica,sans-serif;\">"
        + "<div style=\"max-width:660px;margin:30px auto;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);\">"

        // ── HEADER ────────────────────────────────────────────────
        + "<div style=\"background-color:"
        + PRIMARY
        + ";padding:24px 30px;text-align:center;\">"
        + "<h1 style=\"color:#fff;margin:0;font-size:22px;letter-spacing:1px;\">ORDEN DE COMPRA</h1>"
        + "<p style=\"color:#b8d0f4;margin:6px 0 0;font-size:15px;font-weight:bold;\">&#35; "
        + esc(order.getOrderNumber())
        + "</p>"
        + "</div>"

        // ── CLIENTE ───────────────────────────────────────────────
        + "<div style=\"padding:22px 30px;background-color:#f7f9fc;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:"
        + PRIMARY
        + ";margin:0 0 14px;font-size:13px;text-transform:uppercase;letter-spacing:0.5px;\">Informaci&oacute;n del Cliente</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:13px;\">"
        + row("Cliente", clientName)
        + row("Correo", clientEmail)
        + row("NIT / Identificaci&oacute;n", nit)
        + row("Tel&eacute;fono", phone)
        + row("Direcci&oacute;n", address)
        + "</table></div>"

        // ── DATOS DE LA ORDEN ─────────────────────────────────────
        + "<div style=\"padding:22px 30px;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:"
        + PRIMARY
        + ";margin:0 0 14px;font-size:13px;text-transform:uppercase;letter-spacing:0.5px;\">Datos de la Orden</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:13px;\">"
        + row("Fecha de Orden", dateLabel)
        + row("Condici&oacute;n de Pago", paymentLabel)
        + row("Descuentos", discountStr)
        + row("Otros descuentos", addDiscountStr)
        + row("Ciudad de Despacho", shipmentCity)
        + row("Direcci&oacute;n de Despacho", shipmentAddress)
        + row("Asesor", asesorName)
        + "</table></div>"

        // ── LISTADO DE PRODUCTOS ──────────────────────────────────
        + "<div style=\"padding:22px 30px;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:"
        + PRIMARY
        + ";margin:0 0 14px;font-size:13px;text-transform:uppercase;letter-spacing:0.5px;\">Listado de Productos</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:12px;\">"
        + "<thead><tr style=\"background-color:"
        + PRIMARY
        + ";\">"
        + "<th style=\"padding:8px 10px;color:#fff;text-align:left;font-weight:bold;\">C&oacute;digo</th>"
        + "<th style=\"padding:8px 10px;color:#fff;text-align:left;font-weight:bold;\">Producto</th>"
        + "<th style=\"padding:8px 10px;color:#fff;text-align:center;font-weight:bold;\">Cant.</th>"
        + "<th style=\"padding:8px 10px;color:#fff;text-align:right;font-weight:bold;\">Subtotal</th>"
        + "<th style=\"padding:8px 10px;color:#fff;text-align:right;font-weight:bold;\">IVA</th>"
        + "<th style=\"padding:8px 10px;color:#fff;text-align:right;font-weight:bold;\">Total</th>"
        + "</tr></thead><tbody>"
        + itemRows
        + "</tbody></table></div>"

        // ── RESUMEN FINANCIERO ────────────────────────────────────
        + "<div style=\"padding:22px 30px;border-bottom:1px solid #e1e6ed;\">"
        + "<h2 style=\"color:"
        + PRIMARY
        + ";margin:0 0 14px;font-size:13px;text-transform:uppercase;letter-spacing:0.5px;\">Resumen Financiero</h2>"
        + "<table style=\"width:100%;border-collapse:collapse;font-size:13px;\">"
        + sumRow("Subtotal", fmt(subtotal), false)
        + sumRow("IVA (19%)", fmt(tax), false)
        + sumRow("Retefuente", "- " + fmt(retefuente), false)
        + sumRow("Descuento", "- " + fmt(discountAmt), false)
        + sumRow("Otros descuentos", "- " + fmt(additionalDiscountAmt), false)
        + sumRow("Flete", fmt(freight), false)
        + sumRow("TOTAL", fmt(total), true)
        + "</table></div>"
        + observationsSection

        // ── FOOTER ────────────────────────────────────────────────
        + "<div style=\"padding:16px 30px;text-align:center;font-size:12px;color:#999;border-top:1px solid #e1e6ed;\">"
        + "<p style=\"margin:0;\">Dispofast &mdash; Sistema de Gesti&oacute;n Log&iacute;stica &mdash; www.dispofast.com</p>"
        + "</div>"
        + "</div></body></html>";
  }

  private static String row(String label, String value) {
    return "<tr>"
        + "<td style=\"padding:5px 0;color:#666;width:40%;\">"
        + label
        + "</td>"
        + "<td style=\"padding:5px 0;color:#222;\">"
        + value
        + "</td>"
        + "</tr>";
  }

  private static String sumRow(String label, String value, boolean highlight) {
    String style =
        highlight
            ? "padding:10px 0;color:"
                + PRIMARY
                + ";font-weight:bold;font-size:14px;border-top:2px solid "
                + PRIMARY
                + ";"
            : "padding:5px 0;color:#666;";
    String valueStyle =
        highlight
            ? "padding:10px 0;text-align:right;color:"
                + PRIMARY
                + ";font-weight:bold;font-size:14px;border-top:2px solid "
                + PRIMARY
                + ";"
            : "padding:5px 0;text-align:right;color:#222;";
    return "<tr>"
        + "<td style=\""
        + style
        + "\">"
        + label
        + "</td>"
        + "<td style=\""
        + valueStyle
        + "\">"
        + value
        + "</td>"
        + "</tr>";
  }

  private static String fmt(BigDecimal value) {
    if (value == null) return "$0,00";
    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "CO"));
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    return "$" + nf.format(value);
  }

  private static String esc(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;");
  }
}
