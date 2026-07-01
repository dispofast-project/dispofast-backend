UPDATE quotes SET status = 'ACCEPTED' WHERE status = 'APPROVED';
-- Some rows were loaded manually with state = 'ACTIVE', a value that only
-- belongs to ProductState, not StockState. Recompute the correct value using
-- the same thresholds InventoryServiceImpl.calculateState() applies at runtime.
UPDATE inventory_stock
SET state = CASE
    WHEN quantity_available <= 0 THEN 'OUT_OF_STOCK'
    WHEN quantity_available <= 10 THEN 'LOW_STOCK'
    ELSE 'IN_STOCK'
END
WHERE state NOT IN ('IN_STOCK', 'LOW_STOCK', 'OUT_OF_STOCK');