ALTER TABLE carriers DROP COLUMN plate;
ALTER TABLE carriers ADD COLUMN website VARCHAR(255);
ALTER TABLE carriers ADD COLUMN registered_at DATE;
