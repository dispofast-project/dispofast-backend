-- TEMPORAL: Agrega órdenes con productos a los despachos de prueba (V42)
-- para poder visualizar la tabla de productos en el panel de detalle.
DO $$
DECLARE
    v_admin_id   UUID;
    v_client1_id UUID := '3fa85f64-5717-4562-b3fc-2c963f66afa6'; -- Distribuciones ABC
    v_client2_id UUID := '11111111-1111-1111-1111-111111111111'; -- Constructora XYZ
    v_pricelist  UUID := '3fa85f64-5717-4562-b3fc-2c963f66afa6'; -- Lista Base (V9)

    -- Productos del seed de inventario (V17)
    p_guantes UUID := '9d3b7cc8-4baf-43c9-9f61-3ad87f0bb3e1'; -- Guantes Nitrilo (IVA 0%)
    p_jeringa UUID := 'f2f4fd25-df57-4a57-b0c0-7c9ac4a9d1d5'; -- Jeringa 10ml   (IVA 0%)
    p_tension UUID := 'b49b7548-c4b0-4c14-a8ad-12952fe2716b'; -- Tensiómetro     (IVA 19%)

    -- IDs de órdenes para las facturas de prueba
    o1  UUID := gen_random_uuid();
    o2  UUID := gen_random_uuid();
    o3  UUID := gen_random_uuid();
    o4  UUID := gen_random_uuid();
    o5  UUID := gen_random_uuid();
    o6  UUID := gen_random_uuid();
    o7  UUID := gen_random_uuid();
    o8  UUID := gen_random_uuid();
    o9  UUID := gen_random_uuid();
    o10 UUID := gen_random_uuid();
BEGIN
    SELECT id INTO v_admin_id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1;

    IF v_admin_id IS NULL THEN
        RAISE NOTICE 'Skipping shipment items seed: admin user not found.';
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM products WHERE id = p_guantes) THEN
        RAISE NOTICE 'Skipping shipment items seed: products from V17 not found.';
        RETURN;
    END IF;

    -- Órdenes de ventas (estado INVOICED, vinculadas a las facturas de prueba)
    INSERT INTO sales_order (id, order_number, client_id, asesor_user_id, state, order_date, total_value, price_list_id)
    VALUES
        (o1,  'ORD-TST-001', v_client1_id, v_admin_id, 'INVOICED', now() - INTERVAL '10 days', 320000,  v_pricelist),
        (o2,  'ORD-TST-002', v_client2_id, v_admin_id, 'INVOICED', now() - INTERVAL '8 days',  180000,  v_pricelist),
        (o3,  'ORD-TST-003', v_client1_id, v_admin_id, 'INVOICED', now() - INTERVAL '6 days',  540000,  v_pricelist),
        (o4,  'ORD-TST-004', v_client2_id, v_admin_id, 'INVOICED', now() - INTERVAL '5 days',  210000,  v_pricelist),
        (o5,  'ORD-TST-005', v_client1_id, v_admin_id, 'INVOICED', now() - INTERVAL '15 days', 670000,  v_pricelist),
        (o6,  'ORD-TST-006', v_client2_id, v_admin_id, 'INVOICED', now() - INTERVAL '20 days', 430000,  v_pricelist),
        (o7,  'ORD-TST-007', v_client1_id, v_admin_id, 'INVOICED', now() - INTERVAL '3 days',  290000,  v_pricelist),
        (o8,  'ORD-TST-008', v_client2_id, v_admin_id, 'INVOICED', now() - INTERVAL '12 days', 760000,  v_pricelist),
        (o9,  'ORD-TST-009', v_client1_id, v_admin_id, 'INVOICED', now() - INTERVAL '25 days', 390000,  v_pricelist),
        (o10, 'ORD-TST-010', v_client2_id, v_admin_id, 'INVOICED', now() - INTERVAL '7 days',  510000,  v_pricelist)
    ON CONFLICT DO NOTHING;

    -- Items por orden
    INSERT INTO sales_order_item (order_id, product_id, quantity, unit_price, discount, line_total) VALUES
        -- FAC-TEST-001: 3 productos
        (o1, p_guantes, 3,  55000, 0,  165000),
        (o1, p_jeringa, 5,  31000, 0,  155000),

        -- FAC-TEST-002: 1 producto
        (o2, p_tension, 1, 180000, 0,  180000),

        -- FAC-TEST-003: 5 productos variados
        (o3, p_guantes, 4,  55000, 0,  220000),
        (o3, p_jeringa, 8,  31000, 0,  248000),
        (o3, p_tension, 1, 180000, 0,  180000),

        -- FAC-TEST-004: 2 productos
        (o4, p_guantes, 2,  55000, 0,  110000),
        (o4, p_tension, 1, 180000, 0,  180000),

        -- FAC-TEST-005: 7 referencias
        (o5, p_guantes, 6,  55000, 0,  330000),
        (o5, p_jeringa, 5,  31000, 0,  155000),
        (o5, p_tension, 1, 180000, 0,  180000),

        -- FAC-TEST-006: 4 productos
        (o6, p_guantes, 5,  55000, 0,  275000),
        (o6, p_jeringa, 5,  31000, 0,  155000),

        -- FAC-TEST-007: 2 productos
        (o7, p_jeringa, 4,  31000, 0,  124000),
        (o7, p_tension, 1, 180000, 0,  180000),

        -- FAC-TEST-008: 6 productos variados
        (o8, p_guantes, 4,  55000, 0,  220000),
        (o8, p_jeringa, 6,  31000, 0,  186000),
        (o8, p_tension, 2, 180000, 0,  360000),

        -- FAC-TEST-009: 3 productos
        (o9, p_guantes, 5,  55000, 0,  275000),
        (o9, p_jeringa, 5,  31000, 0,  155000),

        -- FAC-TEST-010: 8 referencias
        (o10, p_guantes, 3,  55000, 0, 165000),
        (o10, p_jeringa, 6,  31000, 0, 186000),
        (o10, p_tension, 1, 180000, 0, 180000)
    ON CONFLICT DO NOTHING;

    -- Vincular las facturas de prueba con sus órdenes
    UPDATE invoices SET sales_order_id = o1  WHERE invoice_number = 'FAC-TEST-001' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o2  WHERE invoice_number = 'FAC-TEST-002' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o3  WHERE invoice_number = 'FAC-TEST-003' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o4  WHERE invoice_number = 'FAC-TEST-004' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o5  WHERE invoice_number = 'FAC-TEST-005' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o6  WHERE invoice_number = 'FAC-TEST-006' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o7  WHERE invoice_number = 'FAC-TEST-007' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o8  WHERE invoice_number = 'FAC-TEST-008' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o9  WHERE invoice_number = 'FAC-TEST-009' AND sales_order_id IS NULL;
    UPDATE invoices SET sales_order_id = o10 WHERE invoice_number = 'FAC-TEST-010' AND sales_order_id IS NULL;

END $$;
