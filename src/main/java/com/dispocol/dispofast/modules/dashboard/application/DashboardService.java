package com.dispocol.dispofast.modules.dashboard.application;

import com.dispocol.dispofast.modules.dashboard.api.dtos.DashboardStatsDto;

public interface DashboardService {
  DashboardStatsDto getStats();
}
