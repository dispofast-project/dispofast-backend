package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeQuoteStatusRequestDTO {
  @NotNull private QuoteStatus status;
}
