-- Base table for customers, aligned with domain fields
CREATE TABLE IF NOT EXISTS customers (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	social_reason VARCHAR(255) NOT NULL,
	nit_cedula VARCHAR(100) NOT NULL,
	witholding_tax BOOLEAN NOT NULL,
	zone VARCHAR(100),
	address VARCHAR(255),
	city VARCHAR(100),
	country VARCHAR(100),
	depto VARCHAR(100),
	phone VARCHAR(50),
	email VARCHAR(255),
	email_fact_elec VARCHAR(255),
	classification VARCHAR(100),
	type_client VARCHAR(100),
	origin VARCHAR(100),
	state VARCHAR(100),

	-- Relation to AppUser (@ManyToOne user)
	user_id UUID NOT NULL,
	CONSTRAINT fk_customer_user FOREIGN KEY (user_id)
		REFERENCES users(id) ON DELETE CASCADE
);

-- Helpful indexes for lookups and joins
CREATE INDEX IF NOT EXISTS idx_customers_user_id ON customers(user_id);
CREATE INDEX IF NOT EXISTS idx_customers_nit_cedula ON customers(nit_cedula);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);

-- Note: Related tables (customer_contacts, customer_legal_docs, credit_profiles)
-- will be created in subsequent migrations when their domain fields are finalized.

