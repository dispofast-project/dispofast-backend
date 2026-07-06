package com.dispocol.dispofast.modules.cartera.api.controllers;

import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryFilterDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CarteraStatsDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreateManualArEntryRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreatePaymentReceiptRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import com.dispocol.dispofast.modules.cartera.application.interfaces.ArEntryService;
import com.dispocol.dispofast.modules.cartera.application.interfaces.PaymentReceiptService;
import com.dispocol.dispofast.modules.cartera.domain.ArEntryState;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cartera")
@RequiredArgsConstructor
public class CarteraController {

  private final ArEntryService arEntryService;
  private final PaymentReceiptService paymentReceiptService;

  /**
   * Lista la cartera con filtros opcionales. VENDEDOR ve solo los clientes que tiene asignados.
   * ADMIN ve todo.
   */
  @GetMapping
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<Page<ArEntryResponseDTO>> getCartera(
      Pageable pageable,
      @RequestParam(required = false) UUID clientId,
      @RequestParam(required = false) UUID asesorUserId,
      @RequestParam(required = false) ArEntryState state,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaInicio,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaFin,
      @RequestParam(required = false) String search) {
    ArEntryFilterDTO filter = new ArEntryFilterDTO();
    filter.setClientId(clientId);
    filter.setAsesorUserId(asesorUserId);
    filter.setState(state);
    filter.setFechaInicio(fechaInicio);
    filter.setFechaFin(fechaFin);
    filter.setSearch(search);
    return ResponseEntity.ok(arEntryService.getArEntries(pageable, filter));
  }

  /**
   * Totales de cartera (total y vencida) calculados en base de datos, respetando la misma
   * visibilidad por rol que {@link #getCartera}. Reemplaza el cálculo que antes se hacía en el
   * frontend trayendo cientos de registros completos.
   */
  @GetMapping("/stats")
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<CarteraStatsDTO> getStats() {
    return ResponseEntity.ok(arEntryService.getStats());
  }

  /** Nombres de asesores con entradas de cartera, para el filtro de la UI. */
  @GetMapping("/asesores")
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<List<String>> getAsesorNames() {
    return ResponseEntity.ok(arEntryService.getAsesorNames());
  }

  /** Crea un registro manual de cartera (Osteosíntesis). Solo ADMIN. */
  @PostMapping("/manual")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ArEntryResponseDTO> createManualEntry(
      @Valid @RequestBody CreateManualArEntryRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(arEntryService.createManualEntry(request));
  }

  /** Crea un recibo de caja sobre una entrada de cartera. */
  @PostMapping("/{arEntryId}/recibos")
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<PaymentReceiptResponseDTO> createReceipt(
      @PathVariable UUID arEntryId, @Valid @RequestBody CreatePaymentReceiptRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(paymentReceiptService.createReceipt(arEntryId, request));
  }

  /** Lista los recibos de caja de una entrada de cartera. */
  @GetMapping("/{arEntryId}/recibos")
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<List<PaymentReceiptResponseDTO>> getReceipts(@PathVariable UUID arEntryId) {
    return ResponseEntity.ok(paymentReceiptService.getReceiptsByArEntry(arEntryId));
  }

  /** Obtiene un recibo de caja por su ID. */
  @GetMapping("/recibos/{receiptId}")
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<PaymentReceiptResponseDTO> getReceiptById(@PathVariable UUID receiptId) {
    return ResponseEntity.ok(paymentReceiptService.getReceiptById(receiptId));
  }

  @GetMapping("/total-value")
  public double getTotalPaidValue() {
    return paymentReceiptService.getTotalPaidValue();
  }

  /** Sube un comprobante de pago a S3 y retorna la key. */
  @PostMapping(value = "/vouchers/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<String> uploadVoucher(@RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(paymentReceiptService.uploadVoucher(file));
  }

  /** Descarga el comprobante de pago de un recibo. */
  @GetMapping("/recibos/{receiptId}/voucher")
  @PreAuthorize("hasAuthority('ACCOUNTS_VIEW')")
  public ResponseEntity<byte[]> downloadVoucher(@PathVariable UUID receiptId) {
    byte[] data = paymentReceiptService.downloadVoucher(receiptId);
    String filename = paymentReceiptService.getVoucherFilename(receiptId);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    return ResponseEntity.ok().headers(headers).body(data);
  }
}
