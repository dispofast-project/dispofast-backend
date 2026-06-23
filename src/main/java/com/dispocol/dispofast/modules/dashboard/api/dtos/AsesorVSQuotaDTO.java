package com.dispocol.dispofast.modules.dashboard.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AsesorVSQuotaDTO(
    UUID asesorId,
    String asesorFullName,
    int year,
    int month,
    BigDecimal ventas,
    BigDecimal cuota,
    double pctCumplimiento) {}
