-- Departments table (matches Department entity)
CREATE TABLE departments (
    code VARCHAR(5) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX idx_dept_name ON departments(name);

-- Cities table (matches City entity)
CREATE TABLE cities (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    department_code VARCHAR(5) NOT NULL,
    CONSTRAINT fk_city_department FOREIGN KEY (department_code) REFERENCES departments(code) ON DELETE RESTRICT
);

CREATE INDEX idx_city_name ON cities(name);
CREATE INDEX idx_city_dept_code ON cities(department_code);

-- Seed: fictional test departments (codes that don't exist in real Colombian data)
INSERT INTO departments (code, name) VALUES ('99', 'Depto Test A');
INSERT INTO departments (code, name) VALUES ('98', 'Depto Test B');

-- Seed: fictional test cities referenced by client seed data
INSERT INTO cities (code, name, department_code) VALUES ('99001', 'Ciudad Test A', '99');
INSERT INTO cities (code, name, department_code) VALUES ('98001', 'Ciudad Test B', '98');
