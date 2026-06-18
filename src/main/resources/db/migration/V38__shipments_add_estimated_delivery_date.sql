ALTER TABLE shipments
  ADD COLUMN IF NOT EXISTS estimated_delivery_date DATE;
