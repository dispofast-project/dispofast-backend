package com.dispocol.dispofast.modules.orders.api.controllers;

import com.dispocol.dispofast.modules.orders.api.dtos.CreateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderFilterDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderResponseDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.UpdateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.application.interfaces.SalesOrderService;
import com.dispocol.dispofast.modules.orders.domain.OrderState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class SalesOrderController {

  private final SalesOrderService salesOrderService;

  @PostMapping
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_CREATE')")
  public ResponseEntity<SalesOrderResponseDTO> createSalesOrder(
      @Valid @RequestBody CreateSalesOrderRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(salesOrderService.createSalesOrder(request));
  }

  @PostMapping("/from-quote/{quoteId}")
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_CREATE')")
  public ResponseEntity<SalesOrderResponseDTO> createFromQuote(@PathVariable UUID quoteId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(salesOrderService.createSalesOrderFromQuote(quoteId));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_VIEW')")
  public ResponseEntity<SalesOrderResponseDTO> getSalesOrderById(@PathVariable UUID id) {
    return ResponseEntity.ok(salesOrderService.getSalesOrderById(id));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_VIEW')")
  public ResponseEntity<Page<SalesOrderResponseDTO>> getAllSalesOrders(
      Pageable pageable,
      @RequestParam(required = false) OrderState state,
      @RequestParam(required = false) UUID clientId,
      @RequestParam(required = false) UUID asesorUserId,
      @RequestParam(required = false) String orderNumber) {
    SalesOrderFilterDTO filter = new SalesOrderFilterDTO();
    filter.setState(state);
    filter.setClientId(clientId);
    filter.setAsesorUserId(asesorUserId);
    filter.setOrderNumber(orderNumber);
    return ResponseEntity.ok(salesOrderService.getAllSalesOrders(pageable, filter));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_EDIT')")
  public ResponseEntity<SalesOrderResponseDTO> updateSalesOrder(
      @PathVariable UUID id, @Valid @RequestBody UpdateSalesOrderRequestDTO request) {
    return ResponseEntity.ok(salesOrderService.updateSalesOrder(id, request));
  }

  @PatchMapping(value = "/{id}/invoice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_EDIT')")
  public ResponseEntity<SalesOrderResponseDTO> attachInvoice(
      @PathVariable UUID id,
      @RequestParam("invoiceNumber") @NotBlank String invoiceNumber,
      @RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(salesOrderService.attachInvoice(id, invoiceNumber, file));
  }

  @GetMapping("/{id}/invoice/download")
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_VIEW')")
  public ResponseEntity<byte[]> downloadInvoice(@PathVariable UUID id) {
    byte[] data = salesOrderService.downloadInvoice(id);
    String fileName = salesOrderService.getInvoiceFileName(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
    headers.setContentType(MediaType.APPLICATION_PDF);
    return ResponseEntity.ok().headers(headers).body(data);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_DELETE')")
  public ResponseEntity<Void> deleteSalesOrder(@PathVariable UUID id) {
    salesOrderService.deleteSalesOrder(id);
    return ResponseEntity.noContent().build();
  }
}
