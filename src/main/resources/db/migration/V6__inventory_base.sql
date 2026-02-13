-- Create categories table
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create products table
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    short_description TEXT NOT NULL,
    long_description TEXT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    tax_free BOOLEAN NOT NULL DEFAULT false,
    sku VARCHAR(100) NOT NULL UNIQUE,
    reference VARCHAR(100) NOT NULL UNIQUE,
    size VARCHAR(50) NOT NULL,
    seo_title VARCHAR(255) NOT NULL,
    seo_description TEXT NOT NULL,
    seo_keywords VARCHAR(500) NOT NULL,
    state VARCHAR(50) NOT NULL,
    category_id UUID NOT NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

-- Create inventory_stock table
CREATE TABLE inventory_stock (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quantity_available INTEGER NOT NULL DEFAULT 0,
    quantity_reserved INTEGER NOT NULL DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    state VARCHAR(50) NOT NULL,
    product_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_state ON products(state);
CREATE INDEX idx_inventory_stock_product_id ON inventory_stock(product_id);
CREATE INDEX idx_inventory_stock_state ON inventory_stock(state);
