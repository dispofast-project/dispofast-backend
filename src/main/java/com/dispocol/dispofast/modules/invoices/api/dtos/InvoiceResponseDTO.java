package com.dispocol.dispofast.modules.invoices.api.dtos;

import com.dispocol.dispofast.modules.invoices.domain.InvoiceState;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class InvoiceResponseDTO {

  private UUID id;
  private UUID salesOrderId;
  private String orderNumber;
  private UUID clientId;
  private String clientName;
  private String invoiceNumber;
  private OffsetDateTime issueDate;
  private BigDecimal totalValue;
  private InvoiceState state;
}
