package com.dispocol.dispofast.modules.dashboard.application;

import com.dispocol.dispofast.modules.dashboard.api.dtos.AsesorVSQuotaDTO;
import com.dispocol.dispofast.modules.dashboard.api.dtos.DashboardStatsDto;
import java.util.List;

public interface DashboardService {
  DashboardStatsDto getStats();

  List<AsesorVSQuotaDTO> getAsesorVSQuotaStats(int months, String type);
}
