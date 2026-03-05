package com.dispocol.dispofast.unit.iam.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.PermissionOverrideDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserPermissionsDetailDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapperImpl;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.domain.Permission;
import com.dispocol.dispofast.modules.iam.domain.Role;
import com.dispocol.dispofast.modules.iam.domain.UserPermission;
import com.dispocol.dispofast.modules.iam.domain.UserPermissionId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserMapper")
class UserMapperTest {

  private final UserMapperImpl userMapper = new UserMapperImpl();

  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID PERMISSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

  private Role role;
  private Permission permission;
  private AppUser user;

  @BeforeEach
  void setUp() {
    permission = new Permission();
    permission.setId(PERMISSION_ID);
    permission.setName("INVENTORY_VIEW");

    role = new Role();
    role.setId(ROLE_ID);
    role.setName("VENDEDOR");
    role.setPermissions(Set.of(permission));

    user = new AppUser();
    user.setId(USER_ID);
    user.setFullName("Carlos Pérez");
    user.setEmail("carlos@dispocol.com");
    user.setActive(true);
    user.setRoles(Set.of(role));
    user.setPermissions(new HashSet<>());
  }

  // -------------------------------------------------------
  // toUserResponseDTO
  // -------------------------------------------------------

  @Nested
  @DisplayName("toUserResponseDTO")
  class ToUserResponseDTO {

    @Test
    @DisplayName("mapea los campos básicos correctamente")
    void shouldMapBasicFields() {
      UserResponseDTO dto = userMapper.toUserResponseDTO(user);

      assertThat(dto.getId()).isEqualTo(USER_ID.toString());
      assertThat(dto.getName()).isEqualTo("Carlos Pérez");
      assertThat(dto.getEmail()).isEqualTo("carlos@dispocol.com");
    }

    @Test
    @DisplayName("mapea el nombre del rol correctamente")
    void shouldMapRoleName() {
      UserResponseDTO dto = userMapper.toUserResponseDTO(user);

      assertThat(dto.getRole()).isEqualTo("VENDEDOR");
    }

    @Test
    @DisplayName("retorna null en role cuando el usuario no tiene roles")
    void shouldReturnNullRoleWhenNoRoles() {
      user.setRoles(new java.util.HashSet<>());
      UserResponseDTO dto = userMapper.toUserResponseDTO(user);

      assertThat(dto.getRole()).isNull();
    }

    @Test
    @DisplayName("resuelve los permisos efectivos heredados del rol")
    void shouldResolveEffectivePermissions() {
      UserResponseDTO dto = userMapper.toUserResponseDTO(user);

      assertThat(dto.getEffectivePermissions()).containsExactly("INVENTORY_VIEW");
    }

    @Test
    @DisplayName("aplica override granted=false revocando el permiso del rol")
    void shouldApplyDenyOverride() {
      UserPermission override = buildOverride(user, permission, false);
      user.setPermissions(Set.of(override));

      UserResponseDTO dto = userMapper.toUserResponseDTO(user);

      assertThat(dto.getEffectivePermissions()).doesNotContain("INVENTORY_VIEW");
    }

    @Test
    @DisplayName("aplica override granted=true sumando un permiso extra")
    void shouldApplyGrantOverride() {
      Permission extra = new Permission();
      extra.setId(UUID.randomUUID());
      extra.setName("QUOTES_VIEW");

      UserPermission override = buildOverride(user, extra, true);
      user.setPermissions(Set.of(override));

      UserResponseDTO dto = userMapper.toUserResponseDTO(user);

      assertThat(dto.getEffectivePermissions()).contains("INVENTORY_VIEW", "QUOTES_VIEW");
    }
  }

  // -------------------------------------------------------
  // toUserPermissionsDetailDTO
  // -------------------------------------------------------

  @Nested
  @DisplayName("toUserPermissionsDetailDTO")
  class ToUserPermissionsDetailDTO {

    @Test
    @DisplayName("mapea userId, userName y role correctamente")
    void shouldMapHeaderFields() {
      UserPermissionsDetailDTO dto = userMapper.toUserPermissionsDetailDTO(user);

      assertThat(dto.getUserId()).isEqualTo(USER_ID.toString());
      assertThat(dto.getUserName()).isEqualTo("Carlos Pérez");
      assertThat(dto.getRole()).isEqualTo("VENDEDOR");
    }

    @Test
    @DisplayName("retorna overrides vacíos cuando el usuario no tiene ninguno")
    void shouldReturnEmptyOverridesWhenNone() {
      UserPermissionsDetailDTO dto = userMapper.toUserPermissionsDetailDTO(user);

      assertThat(dto.getOverrides()).isEmpty();
    }

    @Test
    @DisplayName("mapea cada override con permissionId, permissionName y granted")
    void shouldMapOverridesCorrectly() {
      UserPermission override = buildOverride(user, permission, false);
      user.setPermissions(Set.of(override));

      UserPermissionsDetailDTO dto = userMapper.toUserPermissionsDetailDTO(user);

      assertThat(dto.getOverrides()).hasSize(1);
      PermissionOverrideDTO mapped = dto.getOverrides().iterator().next();
      assertThat(mapped.getPermissionId()).isEqualTo(PERMISSION_ID);
      assertThat(mapped.getPermissionName()).isEqualTo("INVENTORY_VIEW");
      assertThat(mapped.getGranted()).isFalse();
    }
  }

  // -------------------------------------------------------
  // fromCreateUserRequestDTO
  // -------------------------------------------------------

  @Nested
  @DisplayName("fromCreateUserRequestDTO")
  class FromCreateUserRequestDTO {

    @Test
    @DisplayName("recorta espacios y normaliza el email a minúsculas")
    void shouldTrimAndNormalizeFields() {
      CreateUserRequestDTO dto =
          new CreateUserRequestDTO(
              "  Jane Smith  ", "  JANE@EMAIL.COM  ", "securePassword1", ROLE_ID);

      AppUser result = userMapper.fromCreateUserRequestDTO(dto);

      assertThat(result.getFullName()).isEqualTo("Jane Smith");
      assertThat(result.getEmail()).isEqualTo("jane@email.com");
    }

    @Test
    @DisplayName("no asigna rol — el servicio lo hace después del mapeo")
    void shouldNotMapRole() {
      CreateUserRequestDTO dto =
          new CreateUserRequestDTO("Jane Smith", "jane@email.com", "securePassword1", ROLE_ID);

      AppUser result = userMapper.fromCreateUserRequestDTO(dto);

      assertThat(result.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("no asigna passwordHash — el servicio lo encripta después")
    void shouldNotMapPasswordHash() {
      CreateUserRequestDTO dto =
          new CreateUserRequestDTO("Jane Smith", "jane@email.com", "securePassword1", ROLE_ID);

      AppUser result = userMapper.fromCreateUserRequestDTO(dto);

      assertThat(result.getPasswordHash()).isNull();
    }

    @Test
    @DisplayName("id, createdAt y updatedAt quedan nulos tras el mapeo")
    void shouldLeaveAuditFieldsNull() {
      CreateUserRequestDTO dto =
          new CreateUserRequestDTO("Jane Smith", "jane@email.com", "securePassword1", ROLE_ID);

      AppUser result = userMapper.fromCreateUserRequestDTO(dto);

      assertThat(result.getId()).isNull();
      assertThat(result.getCreatedAt()).isNull();
      assertThat(result.getUpdatedAt()).isNull();
    }
  }

  // -------------------------------------------------------
  // Helper
  // -------------------------------------------------------

  private UserPermission buildOverride(AppUser user, Permission permission, boolean granted) {
    UserPermission override = new UserPermission();
    override.setId(new UserPermissionId(user.getId(), permission.getId()));
    override.setUser(user);
    override.setPermission(permission);
    override.setGranted(granted);
    return override;
  }
}
