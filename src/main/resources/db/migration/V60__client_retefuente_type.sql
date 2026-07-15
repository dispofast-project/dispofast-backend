ALTER TABLE clients ADD COLUMN retefuente_type VARCHAR(20) NOT NULL DEFAULT 'NO_APLICA';

UPDATE clients c SET retefuente_type = 'PERSONA_JURIDICA'
  WHERE c.retefuente_applies = true AND EXISTS (SELECT 1 FROM organizations o WHERE o.id = c.id);

UPDATE clients c SET retefuente_type = 'PERSONA_NATURAL'
  WHERE c.retefuente_applies = true AND EXISTS (SELECT 1 FROM individuals i WHERE i.id = c.id);

ALTER TABLE clients DROP COLUMN retefuente_applies;

UPDATE system_params SET valor = 524000.0000 WHERE clave = 'RETEFUENTE_THRESHOLD';

DELETE FROM system_params WHERE clave = 'RETEFUENTE_RATE';

INSERT INTO system_params (clave, valor) VALUES ('RETEFUENTE_RATE_PERSONA_JURIDICA', 0.0250);
INSERT INTO system_params (clave, valor) VALUES ('RETEFUENTE_RATE_PERSONA_NATURAL', 0.0350);
