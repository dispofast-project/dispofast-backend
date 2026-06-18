package com.dispocol.dispofast.modules.dashboard.application;

import com.dispocol.dispofast.modules.cartera.infra.persistence.ArEntryRepository;
import com.dispocol.dispofast.modules.dashboard.api.dtos.AsesorVSQuotaDTO;
import com.dispocol.dispofast.modules.dashboard.api.dtos.DashboardStatsDto;
import com.dispocol.dispofast.modules.dashboard.api.dtos.MonthlySalesDto;
import com.dispocol.dispofast.modules.dashboard.api.dtos.TopProductDto;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserGoalRepository;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderItemRepository;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final SalesOrderRepository salesOrderRepository;
  private final SalesOrderItemRepository salesOrderItemRepository;
  private final ArEntryRepository arEntryRepository;
  private final UserGoalRepository userGoalRepository;

  @Override
  public DashboardStatsDto getStats() {
    LocalDate today = LocalDate.now();

    BigDecimal totalVentasMes =
        salesOrderRepository.getTotalVentasMes(today.getYear(), today.getMonthValue());

    BigDecimal carteraVencida =
        arEntryRepository.getCarteraVencida(OffsetDateTime.now(ZoneOffset.UTC));

    OffsetDateTime sixMonthsAgo =
        today.minusMonths(5).withDayOfMonth(1).atStartOfDay().atOffset(ZoneOffset.UTC);
    List<MonthlySalesDto> ventasPorMes =
        salesOrderRepository.getMonthlySales(sixMonthsAgo).stream()
            .map(
                row ->
                    new MonthlySalesDto(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        (BigDecimal) row[2]))
            .toList();

    List<TopProductDto> productosMasVendidos =
        salesOrderItemRepository.getTopProducts(PageRequest.of(0, 5)).stream()
            .map(row -> new TopProductDto((String) row[0], (BigDecimal) row[1]))
            .toList();

    return new DashboardStatsDto(totalVentasMes, carteraVencida, ventasPorMes, productosMasVendidos);
  }

  @Override
  public List<AsesorVSQuotaDTO> getAsesorVSQuotaStats(int months) {
    LocalDate start = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
    OffsetDateTime startDate = start.atStartOfDay().atOffset(ZoneOffset.UTC);

    Map<String, BigDecimal> salesMap = new HashMap<>();
    salesOrderRepository.getSalesByAsesorAndMonth(startDate).forEach(row -> {
      String key = row[0] + "_" + ((Number) row[2]).intValue() + "_" + ((Number) row[3]).intValue();
      salesMap.put(key, (BigDecimal) row[4]);
    });

    return userGoalRepository
        .findAllSalesQuotaFrom(start.getYear(), start.getMonthValue())
        .stream()
        .map(goal -> {
          UUID asesorId = goal.getUser().getId();
          int year     = goal.getYear();
          int month    = goal.getMonth();
          BigDecimal cuota  = goal.getValue();
          BigDecimal ventas = salesMap.getOrDefault(asesorId + "_" + year + "_" + month, BigDecimal.ZERO);
          double pct = cuota.compareTo(BigDecimal.ZERO) > 0
              ? ventas.divide(cuota, 4, RoundingMode.HALF_UP)
                      .multiply(BigDecimal.valueOf(100))
                      .doubleValue()
              : 0.0;
          return new AsesorVSQuotaDTO(asesorId, goal.getUser().getFullName(), year, month, ventas, cuota, pct);
        })
        .toList();
  }
}
