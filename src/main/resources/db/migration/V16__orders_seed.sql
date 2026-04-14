-- Seed data for sales_order and sales_order_item
-- References: clients (V10/V11), users (V4), cities (V7), price_lists (V9), products (V6), quotes (V13)

DO $$
DECLARE
    v_admin_id      UUID;
    v_client_id     UUID := '3fa85f64-5717-4562-b3fc-2c963f66afa6';  -- Distribuciones ABC (org, V10)
    v_price_list_id UUID := '3fa85f64-5717-4562-b3fc-2c963f66afa6';
    v_product_id    UUID;

    v_order_1_id    UUID := gen_random_uuid();
    v_order_2_id    UUID := gen_random_uuid();
    v_order_3_id    UUID := gen_random_uuid();
BEGIN
    SELECT id INTO v_admin_id   FROM users    WHERE email = 'admin@dispocol.com' LIMIT 1;
    SELECT id INTO v_product_id FROM products LIMIT 1;

    IF v_admin_id IS NULL OR v_product_id IS NULL THEN
        RAISE NOTICE 'Skipping orders seed: required references (users or products) not found.';
        RETURN;
    END IF;

    -- Order 1: PENDING
    INSERT INTO sales_order (
        id, order_number, client_id, asesor_user_id, state,
        order_date, shipment_city_id, shipment_address, zone,
        total_value, price_list_id
    ) VALUES (
        v_order_1_id,
        'ORD-2024-001',
        v_client_id,
        v_admin_id,
        'PENDING',
        now(),
        '11001',
        'Calle 100 # 15-20, Bogotá',
        'NORTE',
        250000.00,
        v_price_list_id
    ) ON CONFLICT DO NOTHING;

    INSERT INTO sales_order_item (order_id, product_id, quantity, unit_price, discount, line_total)
    VALUES (v_order_1_id, v_product_id, 2.00, 100000.00, 0.00, 200000.00),
           (v_order_1_id, v_product_id, 1.00,  50000.00, 0.00,  50000.00)
    ON CONFLICT DO NOTHING;

    -- Order 2: INVOICED
    INSERT INTO sales_order (
        id, order_number, client_id, asesor_user_id, state,
        order_date, shipment_city_id, shipment_address, zone,
        total_value, price_list_id,
        invoice_number, invoice_url
    ) VALUES (
        v_order_2_id,
        'ORD-2024-002',
        v_client_id,
        v_admin_id,
        'INVOICED',
        now() - INTERVAL '5 days',
        '05001',
        'Carrera 43 # 10-30, Medellín',
        'OCCIDENTE',
        480000.00,
        v_price_list_id,
        'FAC-2024-001',
        'https://storage.dispocol.com/invoices/FAC-2024-001.pdf'
    ) ON CONFLICT DO NOTHING;

    INSERT INTO sales_order_item (order_id, product_id, quantity, unit_price, discount, line_total)
    VALUES (v_order_2_id, v_product_id, 4.00, 120000.00, 0.00, 480000.00)
    ON CONFLICT DO NOTHING;

    -- Order 3: DELIVERED
    INSERT INTO sales_order (
        id, order_number, client_id, asesor_user_id, state,
        order_date, shipment_city_id, shipment_address, zone,
        total_value, price_list_id,
        invoice_number, invoice_url
    ) VALUES (
        v_order_3_id,
        'ORD-2024-003',
        '11111111-1111-1111-1111-111111111111',  -- Constructora XYZ (org, V10)
        v_admin_id,
        'DELIVERED',
        now() - INTERVAL '15 days',
        '76001',
        'Avenida 6N # 23-45, Cali',
        'SUR',
        150000.00,
        v_price_list_id,
        'FAC-2024-002',
        'https://storage.dispocol.com/invoices/FAC-2024-002.pdf'
    ) ON CONFLICT DO NOTHING;

    INSERT INTO sales_order_item (order_id, product_id, quantity, unit_price, discount, line_total)
    VALUES (v_order_3_id, v_product_id, 3.00, 50000.00, 0.00, 150000.00)
    ON CONFLICT DO NOTHING;

END $$;
