-- Seed data for medical inventory catalog and stock

DO $$
DECLARE
    v_cat_insumos_id      UUID;
    v_cat_dispositivos_id UUID;
    v_cat_proteccion_id   UUID;

    v_product_1_id UUID := '9d3b7cc8-4baf-43c9-9f61-3ad87f0bb3e1';
    v_product_2_id UUID := 'f2f4fd25-df57-4a57-b0c0-7c9ac4a9d1d5';
    v_product_3_id UUID := 'b49b7548-c4b0-4c14-a8ad-12952fe2716b';
BEGIN
    -- Categories
    INSERT INTO categories (name)
    VALUES ('Insumos Medicos'), ('Dispositivos Medicos'), ('Elementos de Proteccion')
    ON CONFLICT (name) DO NOTHING;

    SELECT id INTO v_cat_insumos_id      FROM categories WHERE name = 'Insumos Medicos' LIMIT 1;
    SELECT id INTO v_cat_dispositivos_id FROM categories WHERE name = 'Dispositivos Medicos' LIMIT 1;
    SELECT id INTO v_cat_proteccion_id   FROM categories WHERE name = 'Elementos de Proteccion' LIMIT 1;

    IF v_cat_insumos_id IS NULL OR v_cat_dispositivos_id IS NULL OR v_cat_proteccion_id IS NULL THEN
        RAISE NOTICE 'Skipping inventory seed: required categories were not found.';
        RETURN;
    END IF;

    -- Products
    INSERT INTO products (
        id,
        name,
        short_description,
        long_description,
        image_url,
        tax_free,
        sku,
        reference,
        size,
        seo_title,
        seo_description,
        seo_keywords,
        state,
        category_id
    )
    VALUES
        (
            v_product_1_id,
            'Guantes de Nitrilo Talla M Caja x100',
            'Guantes desechables para proteccion clinica',
            'Guantes de nitrilo sin polvo, resistentes a perforaciones y aptos para procedimientos medicos, laboratorio y atencion general.',
            'https://images.dispofast.local/products/guantes-nitrilo-m-100.jpg',
            true,
            'MED-GNT-NIT-M100',
            'REF-MED-001',
            'Caja x100',
            'guantes-nitrilo-talla-m-caja-100',
            'Compra guantes de nitrilo talla M para uso medico y hospitalario.',
            'guantes nitrilo,desechables,insumos medicos,proteccion',
            'ACTIVE',
            v_cat_proteccion_id
        ),
        (
            v_product_2_id,
            'Jeringa Esteril 10 ml con Aguja 21G',
            'Jeringa desechable esteril de uso clinico',
            'Jeringa de 10 ml con aguja 21G, esterilizada y empacada individualmente para administracion segura de medicamentos.',
            'https://images.dispofast.local/products/jeringa-10ml-21g.jpg',
            true,
            'MED-JRG-10ML-21G',
            'REF-MED-002',
            '10 ML',
            'jeringa-esteril-10-ml-aguja-21g',
            'Jeringa esteril de 10 ml con aguja 21G para procedimientos medicos.',
            'jeringa esteril,10 ml,aguja 21g,insumo hospitalario',
            'ACTIVE',
            v_cat_insumos_id
        ),
        (
            v_product_3_id,
            'Tensiometro Digital de Brazo',
            'Monitor automatico de presion arterial',
            'Dispositivo digital para medicion de presion arterial con pantalla LCD, memoria de lecturas y deteccion de pulso irregular.',
            'https://images.dispofast.local/products/tensiometro-digital-brazo.jpg',
            false,
            'MED-TNS-DIG-BRZ',
            'REF-MED-003',
            'Unidad',
            'tensiometro-digital-de-brazo',
            'Tensiometro digital de brazo para control de presion arterial en casa y clinicas.',
            'tensiometro digital,presion arterial,dispositivo medico,salud',
            'ACTIVE',
            v_cat_dispositivos_id
        )
    ON CONFLICT DO NOTHING;

    -- Inventory stock
    INSERT INTO inventory_stock (product_id, quantity_available, quantity_reserved, state)
    VALUES
        (v_product_1_id, 250, 20, 'IN_STOCK'),
        (v_product_2_id, 180, 15, 'IN_STOCK'),
        (v_product_3_id, 60,  4,  'IN_STOCK')
    ON CONFLICT (product_id) DO NOTHING;

END $$;