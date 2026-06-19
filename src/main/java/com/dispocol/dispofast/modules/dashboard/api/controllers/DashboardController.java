package com.dispocol.dispofast.modules.dashboard.api.controllers;

import com.dispocol.dispofast.modules.dashboard.api.dtos.AsesorVSQuotaDTO;
import com.dispocol.dispofast.modules.dashboard.api.dtos.DashboardStatsDto;
import com.dispocol.dispofast.modules.dashboard.application.DashboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/stats")
  public ResponseEntity<DashboardStatsDto> getStats() {
    return ResponseEntity.ok(dashboardService.getStats());
  }

  @GetMapping("/asesor-vs-quota")
  public ResponseEntity<List<AsesorVSQuotaDTO>> getAsesorVSQuotaStats(
      @RequestParam(defaultValue = "6") int months, @RequestParam(defaultValue = "SALES_QUOTA") String type) {
    return ResponseEntity.ok(dashboardService.getAsesorVSQuotaStats(months, type));
  }
}
