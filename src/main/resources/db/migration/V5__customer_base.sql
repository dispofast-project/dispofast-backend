-- Base table for customers, aligned with domain fields
CREATE TABLE IF NOT EXISTS customers (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	type_person VARCHAR(100) NOT NULL DEFAULT 'NATURAL',
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

-- Customer contacts
CREATE TABLE IF NOT EXISTS customer_contacts (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	contact_name VARCHAR(255) NOT NULL,
	contact_last_name VARCHAR(255) NOT NULL,
	position VARCHAR(255),
	phone VARCHAR(50),
	email VARCHAR(255),

	customer_id UUID NOT NULL,
	CONSTRAINT fk_customer_contact_customer FOREIGN KEY (customer_id)
		REFERENCES customers(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_customer_contacts_customer_id ON customer_contacts(customer_id);
CREATE INDEX IF NOT EXISTS idx_customer_contacts_email ON customer_contacts(email);

-- Customer legal documents
CREATE TABLE IF NOT EXISTS customer_legal_docs (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	doc_type VARCHAR(100),
	issued_at TIMESTAMPTZ,
	expires_at TIMESTAMPTZ,

	customer_id UUID NOT NULL,
	CONSTRAINT fk_customer_legal_doc_customer FOREIGN KEY (customer_id)
		REFERENCES customers(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_customer_legal_docs_customer_id ON customer_legal_docs(customer_id);
CREATE INDEX IF NOT EXISTS idx_customer_legal_docs_doc_type ON customer_legal_docs(doc_type);

-- Credit profiles
CREATE TABLE IF NOT EXISTS credit_profiles (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	credit_days INT NOT NULL,
	credit_limit DOUBLE PRECISION NOT NULL,
	approved BOOLEAN NOT NULL,
	approver VARCHAR(255),
	approval_date DATE,

	customer_id UUID NOT NULL,
	CONSTRAINT fk_credit_profile_customer FOREIGN KEY (customer_id)
		REFERENCES customers(id) ON DELETE CASCADE,
	CONSTRAINT uq_credit_profile_customer UNIQUE (customer_id)
);

CREATE INDEX IF NOT EXISTS idx_credit_profiles_customer_id ON credit_profiles(customer_id);

