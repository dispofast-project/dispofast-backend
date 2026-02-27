CREATE TABLE persons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID,
    identification_number VARCHAR(20),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    job_title VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    retefuente_applies BOOLEAN,
    is_active BOOLEAN,
    category_id BIGINT,
    CONSTRAINT fk_person_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_person_category FOREIGN KEY (category_id) REFERENCES organization_categories(id)
);

INSERT INTO persons (id, organization_id, identification_number, first_name, last_name, job_title, email, phone, retefuente_applies, is_active, category_id)
VALUES (
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    '1020304050',
    'Carlos',
    'Ramírez',
    'Gerente Comercial',
    'carlos.ramirez@abc.com',
    '3001234567',
    false,
    true,
    1
);

INSERT INTO persons (id, organization_id, identification_number, first_name, last_name, job_title, email, phone, retefuente_applies, is_active, category_id)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    '9080706050',
    'María',
    'González',
    'Directora de Compras',
    'maria.gonzalez@xyz.com',
    '3109876543',
    true,
    true,
    2
);
