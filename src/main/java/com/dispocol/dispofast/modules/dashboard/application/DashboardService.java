package com.dispocol.dispofast.modules.dashboard.application;

import java.util.List;

import com.dispocol.dispofast.modules.dashboard.api.dtos.AsesorVSQuotaDTO;
import com.dispocol.dispofast.modules.dashboard.api.dtos.DashboardStatsDto;

public interface DashboardService {
  DashboardStatsDto getStats();
  List<AsesorVSQuotaDTO> getAsesorVSQuotaStats(int months, String type);
}
