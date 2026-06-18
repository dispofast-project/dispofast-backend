-- TEMPORAL: Seed de despachos de prueba para todas las categorías
DO $$
DECLARE
    v_admin_id      UUID;
    v_client1_id    UUID := '3fa85f64-5717-4562-b3fc-2c963f66afa6'; -- Distribuciones ABC
    v_client2_id    UUID := '11111111-1111-1111-1111-111111111111'; -- Constructora XYZ

    v_carrier1_id   UUID := gen_random_uuid();
    v_carrier2_id   UUID := gen_random_uuid();
    v_carrier3_id   UUID := gen_random_uuid();

    v_inv1  UUID := gen_random_uuid();
    v_inv2  UUID := gen_random_uuid();
    v_inv3  UUID := gen_random_uuid();
    v_inv4  UUID := gen_random_uuid();
    v_inv5  UUID := gen_random_uuid();
    v_inv6  UUID := gen_random_uuid();
    v_inv7  UUID := gen_random_uuid();
    v_inv8  UUID := gen_random_uuid();
    v_inv9  UUID := gen_random_uuid();
    v_inv10 UUID := gen_random_uuid();
BEGIN
    SELECT id INTO v_admin_id FROM users WHERE email = 'admin@dispocol.com' LIMIT 1;

    IF v_admin_id IS NULL THEN
        RAISE NOTICE 'Skipping shipments seed: admin user not found.';
        RETURN;
    END IF;

    -- Carriers
    INSERT INTO carriers (id, name, plate) VALUES
        (v_carrier1_id, 'Juan Pérez',      'ABC123'),
        (v_carrier2_id, 'TransRápido S.A.', 'XYZ789'),
        (v_carrier3_id, 'Carlos Rueda',    'DEF456')
    ON CONFLICT (plate) DO NOTHING;

    -- Invoices (dummy, sin orden asociada)
    INSERT INTO invoices (id, invoice_number, client_id, issue_date, total_value, state) VALUES
        (v_inv1,  'FAC-TEST-001', v_client1_id, now() - INTERVAL '10 days', 320000, 'ISSUED'),
        (v_inv2,  'FAC-TEST-002', v_client2_id, now() - INTERVAL '8 days',  180000, 'ISSUED'),
        (v_inv3,  'FAC-TEST-003', v_client1_id, now() - INTERVAL '6 days',  540000, 'ISSUED'),
        (v_inv4,  'FAC-TEST-004', v_client2_id, now() - INTERVAL '5 days',  210000, 'ISSUED'),
        (v_inv5,  'FAC-TEST-005', v_client1_id, now() - INTERVAL '15 days', 670000, 'ISSUED'),
        (v_inv6,  'FAC-TEST-006', v_client2_id, now() - INTERVAL '20 days', 430000, 'ISSUED'),
        (v_inv7,  'FAC-TEST-007', v_client1_id, now() - INTERVAL '3 days',  290000, 'ISSUED'),
        (v_inv8,  'FAC-TEST-008', v_client2_id, now() - INTERVAL '12 days', 760000, 'ISSUED'),
        (v_inv9,  'FAC-TEST-009', v_client1_id, now() - INTERVAL '25 days', 390000, 'ISSUED'),
        (v_inv10, 'FAC-TEST-010', v_client2_id, now() - INTERVAL '7 days',  510000, 'ISSUED')
    ON CONFLICT DO NOTHING;

    -- Shipments: 2 PENDIENTES
    INSERT INTO shipments (invoice_id, state, delivery_address, city_code, invoice_number, created_at, client_name, asesor_name, product_count)
    VALUES
        (v_inv1, 'PENDING', 'Calle 100 # 15-20, Bogotá',      '11001', 'FAC-TEST-001', now() - INTERVAL '10 days', 'Distribuciones ABC', 'Admin Dispocol', 3),
        (v_inv2, 'PENDING', 'Carrera 7 # 45-10, Bogotá',       '11001', 'FAC-TEST-002', now() - INTERVAL '8 days',  'Constructora XYZ',  'Admin Dispocol', 1);

    -- Shipments: 2 ASIGNADOS
    INSERT INTO shipments (invoice_id, state, delivery_address, city_code, carrier_id, invoice_number, created_at, client_name, asesor_name, estimated_delivery_date, product_count)
    VALUES
        (v_inv3, 'ASSIGNED', 'Av El Poblado # 12-34, Medellín', '05001', v_carrier1_id, 'FAC-TEST-003', now() - INTERVAL '6 days',  'Distribuciones ABC', 'Admin Dispocol', (CURRENT_DATE + 2), 5),
        (v_inv4, 'ASSIGNED', 'Calle 10 # 5-60, Barranquilla',   '08001', v_carrier2_id, 'FAC-TEST-004', now() - INTERVAL '5 days',  'Constructora XYZ',  'Admin Dispocol', (CURRENT_DATE + 4), 2);

    -- Shipments: 2 EN RUTA
    INSERT INTO shipments (invoice_id, state, delivery_address, city_code, carrier_id, invoice_number, created_at, client_name, asesor_name, estimated_delivery_date, departure_date, product_count)
    VALUES
        (v_inv5, 'IN_ROUTE', 'Av 6N # 23-45, Cali',              '76001', v_carrier1_id, 'FAC-TEST-005', now() - INTERVAL '15 days', 'Distribuciones ABC', 'Admin Dispocol', (CURRENT_DATE + 1), now() - INTERVAL '2 days', 7),
        (v_inv6, 'IN_ROUTE', 'Carrera 43 # 3-12, Bucaramanga',   '68001', v_carrier3_id, 'FAC-TEST-006', now() - INTERVAL '20 days', 'Constructora XYZ',  'Admin Dispocol', CURRENT_DATE,        now() - INTERVAL '1 day',  4);

    -- Shipments: 2 ENTREGADOS
    INSERT INTO shipments (invoice_id, state, delivery_address, city_code, carrier_id, invoice_number, created_at, client_name, asesor_name, estimated_delivery_date, departure_date, delivery_date, product_count)
    VALUES
        (v_inv7, 'DELIVERED', 'Calle 72 # 10-30, Bogotá',          '11001', v_carrier2_id, 'FAC-TEST-007', now() - INTERVAL '3 days',  'Distribuciones ABC', 'Admin Dispocol', CURRENT_DATE - 1, now() - INTERVAL '3 days', now() - INTERVAL '1 day',  2),
        (v_inv8, 'DELIVERED', 'Transversal 28 # 39-15, Medellín',  '05001', v_carrier1_id, 'FAC-TEST-008', now() - INTERVAL '12 days', 'Constructora XYZ',  'Admin Dispocol', CURRENT_DATE - 3, now() - INTERVAL '8 days', now() - INTERVAL '4 days', 6);

    -- Shipments: 2 RETRASADOS
    INSERT INTO shipments (invoice_id, state, delivery_address, city_code, carrier_id, invoice_number, created_at, client_name, asesor_name, estimated_delivery_date, departure_date, product_count)
    VALUES
        (v_inv9,  'DELAYED', 'Av Boyacá # 60-20, Bogotá',        '11001', v_carrier3_id, 'FAC-TEST-009', now() - INTERVAL '25 days', 'Distribuciones ABC', 'Admin Dispocol', CURRENT_DATE - 5, now() - INTERVAL '18 days', 3),
        (v_inv10, 'DELAYED', 'Calle 30 # 25-10, Cartagena',      '13001', v_carrier2_id, 'FAC-TEST-010', now() - INTERVAL '7 days',  'Constructora XYZ',  'Admin Dispocol', CURRENT_DATE - 2, now() - INTERVAL '5 days',  8);

END $$;
