package com.dispocol.dispofast.modules.dashboard.api.dtos;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsDto(
    BigDecimal totalVentasMes,
    BigDecimal carteraVencida,
    List<MonthlySalesDto> ventasPorMes,
    List<TopProductDto> productosMasVendidos) {}
