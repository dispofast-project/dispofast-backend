package com.dispocol.dispofast.modules.iam.application.interfaces;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserProductAllocationDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserProductAllocationService {

  List<UserProductAllocationDTO> getAllocations(UUID userId);

  UserProductAllocationDTO createAllocation(UUID userId, CreateUserProductAllocationDTO dto);

  UserProductAllocationDTO updateAllocation(
      UUID userId, UUID allocationId, UpdateUserProductAllocationDTO dto);

  void deleteAllocation(UUID userId, UUID allocationId);

  /**
   * Consume {@code quantity} del cupo del vendedor para ese producto. Si el vendedor no tiene
   * ninguna asignación para el producto, no hay restricción y el método no hace nada. Lanza {@code
   * InsufficientAllocationException} si la cantidad solicitada excede el cupo restante.
   */
  void reserveAllocation(UUID userId, UUID productId, BigDecimal quantity);

  /** Devuelve {@code quantity} al cupo del vendedor. No-op si no existe asignación. */
  void releaseAllocation(UUID userId, UUID productId, BigDecimal quantity);
}
