CREATE TABLE drivers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(30),
    cedula VARCHAR(30),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);
