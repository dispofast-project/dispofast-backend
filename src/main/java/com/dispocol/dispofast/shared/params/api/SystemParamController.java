package com.dispocol.dispofast.shared.params.api;

import com.dispocol.dispofast.shared.params.infra.persistence.SystemParamRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system-params")
@RequiredArgsConstructor
public class SystemParamController {

  private final SystemParamRepository systemParamRepository;

  private static final List<String> PUBLIC_KEYS =
      List.of("IVA", "RETEFUENTE_RATE", "RETEFUENTE_THRESHOLD");

  /** Devuelve los parámetros del sistema necesarios para cálculos en el frontend. */
  @GetMapping("/public")
  public ResponseEntity<Map<String, BigDecimal>> getPublicParams() {
    Map<String, BigDecimal> params =
        systemParamRepository.findAll().stream()
            .filter(p -> PUBLIC_KEYS.contains(p.getClave()))
            .collect(Collectors.toMap(p -> p.getClave(), p -> p.getValor()));

    // Fallbacks si algún param no existe en BD
    params.putIfAbsent("IVA", new BigDecimal("0.1900"));
    params.putIfAbsent("RETEFUENTE_RATE", new BigDecimal("0.0250"));
    params.putIfAbsent("RETEFUENTE_THRESHOLD", new BigDecimal("540.0000"));

    return ResponseEntity.ok(params);
  }
}
