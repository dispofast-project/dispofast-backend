-- Seed price_list_items for the 3 seeded products (V17) in both price lists (V9)

DO $$
DECLARE
    v_product_1_id UUID := '9d3b7cc8-4baf-43c9-9f61-3ad87f0bb3e1'; -- Guantes de Nitrilo
    v_product_2_id UUID := 'f2f4fd25-df57-4a57-b0c0-7c9ac4a9d1d5'; -- Jeringa Esteril 10ml
    v_product_3_id UUID := 'b49b7548-c4b0-4c14-a8ad-12952fe2716b'; -- Tensiometro Digital

    v_lista_base_id    UUID := '3fa85f64-5717-4562-b3fc-2c963f66afa6'; -- Lista Base
    v_lista_premium_id UUID := '22222222-2222-2222-2222-222222222222'; -- Lista Premium
BEGIN
    IF NOT EXISTS (SELECT 1 FROM products WHERE id = v_product_1_id) THEN
        RAISE NOTICE 'Skipping price_list_items seed: products not found.';
        RETURN;
    END IF;

    -- Lista Base
    INSERT INTO price_list_items (id, price_list_id, product_id, unit_price)
    VALUES
        (gen_random_uuid(), v_lista_base_id, v_product_1_id, 45000.00),
        (gen_random_uuid(), v_lista_base_id, v_product_2_id,  2500.00),
        (gen_random_uuid(), v_lista_base_id, v_product_3_id, 180000.00)
    ON CONFLICT (price_list_id, product_id) DO NOTHING;

    -- Lista Premium
    INSERT INTO price_list_items (id, price_list_id, product_id, unit_price)
    VALUES
        (gen_random_uuid(), v_lista_premium_id, v_product_1_id, 52000.00),
        (gen_random_uuid(), v_lista_premium_id, v_product_2_id,  3000.00),
        (gen_random_uuid(), v_lista_premium_id, v_product_3_id, 210000.00)
    ON CONFLICT (price_list_id, product_id) DO NOTHING;

END $$;
