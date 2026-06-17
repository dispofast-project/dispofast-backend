package com.dispocol.dispofast.modules.dashboard.application;

import com.dispocol.dispofast.modules.cartera.infra.persistence.ArEntryRepository;
import com.dispocol.dispofast.modules.dashboard.api.dtos.DashboardStatsDto;
import com.dispocol.dispofast.modules.dashboard.api.dtos.MonthlySalesDto;
import com.dispocol.dispofast.modules.dashboard.api.dtos.TopProductDto;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderItemRepository;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

  private final SalesOrderRepository salesOrderRepository;
  private final SalesOrderItemRepository salesOrderItemRepository;
  private final ArEntryRepository arEntryRepository;

  public DashboardServiceImpl(
      SalesOrderRepository salesOrderRepository,
      SalesOrderItemRepository salesOrderItemRepository,
      ArEntryRepository arEntryRepository) {
    this.salesOrderRepository = salesOrderRepository;
    this.salesOrderItemRepository = salesOrderItemRepository;
    this.arEntryRepository = arEntryRepository;
  }

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
}
