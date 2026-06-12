package com.dispocol.dispofast.modules.iam.application.impl;

import com.dispocol.dispofast.modules.iam.api.dtos.PermissionSummaryDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.RoleResponseDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.RoleService;
import com.dispocol.dispofast.modules.iam.domain.Permission;
import com.dispocol.dispofast.modules.iam.infra.persistence.PermissionRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.RoleRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  @Override
  public List<RoleResponseDTO> getAllRoles() {
    List<Permission> allPermissions = permissionRepository.findAll();

    return roleRepository.findAll().stream()
        .map(
            role -> {
              Set<UUID> rolePermIds =
                  role.getPermissions().stream()
                      .map(Permission::getId)
                      .collect(Collectors.toSet());

              Set<PermissionSummaryDTO> permSummaries =
                  allPermissions.stream()
                      .map(
                          p ->
                              new PermissionSummaryDTO(
                                  p.getId(), p.getName(), rolePermIds.contains(p.getId())))
                      .collect(Collectors.toSet());

              return new RoleResponseDTO(role.getId(), role.getName(), permSummaries);
            })
        .collect(Collectors.toList());
  }
}
