package com.dispocol.dispofast.modules.cartera.api.dtos;

import com.dispocol.dispofast.modules.cartera.domain.ArEntryState;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class ArEntryFilterDTO {

  private UUID clientId;
  private UUID asesorUserId;
  private ArEntryState state;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
}
