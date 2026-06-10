package com.dispocol.dispofast.modules.cartera.api.dtos;

import com.dispocol.dispofast.modules.cartera.domain.PaymentMethod;
import com.dispocol.dispofast.modules.cartera.domain.PaymentReceiptState;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class PaymentReceiptResponseDTO {

  private UUID id;
  private String receiptCode;

  private UUID arEntryId;
  private String createdByName;

  private String documentNumber;
  private LocalDate paymentDate;
  private BigDecimal value;
  private PaymentMethod paymentMethod;
  private String voucherS3Key;
  private String observations;
  private PaymentReceiptState state;
  private OffsetDateTime createdAt;
}
