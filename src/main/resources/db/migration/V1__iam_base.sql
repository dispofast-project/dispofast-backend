CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE permissions(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE roles(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE, 
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Permisos base que otorga un rol (herencia)
CREATE TABLE role_permissions (
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role       FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Rol asignado a un usuario
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

-- Overrides individuales por usuario:
--   granted = TRUE  → permiso otorgado explícitamente (suma al rol)
--   granted = FALSE → permiso denegado explícitamente (resta al rol)
CREATE TABLE user_permissions (
    user_id       UUID    NOT NULL,
    permission_id UUID    NOT NULL,
    granted       BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (user_id, permission_id),
    CONSTRAINT fk_up_user       FOREIGN KEY (user_id)       REFERENCES users(id)       ON DELETE CASCADE,
    CONSTRAINT fk_up_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_users_email                    ON users(email);
CREATE INDEX idx_role_permissions_role_id       ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX idx_user_roles_user_id             ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id             ON user_roles(role_id);
CREATE INDEX idx_user_permissions_user_id       ON user_permissions(user_id);
CREATE INDEX idx_user_permissions_permission_id ON user_permissions(permission_id);