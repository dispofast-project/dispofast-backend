ALTER TABLE shipments
    ADD COLUMN driver_id UUID REFERENCES drivers(id),
    ADD COLUMN delivery_type VARCHAR(20),
    ADD COLUMN tracking_code VARCHAR(100),
    ADD COLUMN address_detail VARCHAR(255);
