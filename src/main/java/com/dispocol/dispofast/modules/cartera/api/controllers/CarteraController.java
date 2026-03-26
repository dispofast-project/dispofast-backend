package com.dispocol.dispofast.modules.cartera.api.controllers;

import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryFilterDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreateManualArEntryRequestDTO;
import com.dispocol.dispofast.modules.cartera.application.interfaces.ArEntryService;
import com.dispocol.dispofast.modules.cartera.domain.ArEntryState;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartera")
@RequiredArgsConstructor
public class CarteraController {

  private final ArEntryService arEntryService;

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
          LocalDate fechaFin) {
    ArEntryFilterDTO filter = new ArEntryFilterDTO();
    filter.setClientId(clientId);
    filter.setAsesorUserId(asesorUserId);
    filter.setState(state);
    filter.setFechaInicio(fechaInicio);
    filter.setFechaFin(fechaFin);
    return ResponseEntity.ok(arEntryService.getArEntries(pageable, filter));
  }

  /** Crea un registro manual de cartera (Osteosíntesis). Solo ADMIN. */
  @PostMapping("/manual")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ArEntryResponseDTO> createManualEntry(
      @Valid @RequestBody CreateManualArEntryRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(arEntryService.createManualEntry(request));
  }
}
