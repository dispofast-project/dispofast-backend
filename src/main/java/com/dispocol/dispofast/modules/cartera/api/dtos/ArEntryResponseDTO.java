package com.dispocol.dispofast.modules.cartera.api.dtos;

import com.dispocol.dispofast.modules.cartera.domain.ArEntrySource;
import com.dispocol.dispofast.modules.cartera.domain.ArEntryState;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class ArEntryResponseDTO {

  private UUID id;

  /** Estado de la cartera (PENDING / PAID) */
  private ArEntryState state;

  private ArEntrySource source;

  /** Cliente */
  private String clientName;

  private String clientIdentification;

  /** Asesor */
  private String asesorName;

  /** #Orden */
  private String orderNumber;

  /** Valor */
  private BigDecimal value;

  /** Factura */
  private String invoiceNumber;

  /** Fecha Factura */
  private OffsetDateTime invoiceDate;

  /** Plazo (días de crédito) */
  private int paymentTermDays;

  /** Fecha Vencimiento */
  private OffsetDateTime expirationDate;

  /** Días Cartera: días desde la fecha de factura */
  private long diasCartera;

  /** Días Vencimiento: días pasados desde el vencimiento (0 si no ha vencido) */
  private long diasVencimiento;

  /** Ciudad de despacho */
  private String cityName;
}
