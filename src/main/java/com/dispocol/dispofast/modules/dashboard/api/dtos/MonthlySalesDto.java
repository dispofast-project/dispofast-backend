package com.dispocol.dispofast.modules.dashboard.api.dtos;

import java.math.BigDecimal;

public record MonthlySalesDto(int year, int month, BigDecimal total) {}
