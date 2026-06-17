package com.dispocol.dispofast.modules.iam.application.interfaces;

import com.dispocol.dispofast.modules.iam.api.dtos.RoleResponseDTO;
import java.util.List;

public interface RoleService {

  /**
   * Returns all roles with their full permission set. Each permission includes whether the role
   * grants it by default, enabling the frontend to pre-fill the permissions matrix.
   */
  List<RoleResponseDTO> getAllRoles();
}
