-- CUSTOMERS table
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    pwd VARCHAR(255) NOT NULL
);

-- ROLES table
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- AUTHORITIES table
CREATE TABLE authorities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- CUSTOMER <-> ROLE mapping
CREATE TABLE customer_roles (
    customer_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (customer_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ROLE <-> AUTHORITY mapping
CREATE TABLE role_authorities (
    role_id INT NOT NULL,
    authority_id INT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    CONSTRAINT fk_role_auth_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_auth_auth FOREIGN KEY (authority_id) REFERENCES authorities(id) ON DELETE CASCADE
);

-- Roles
INSERT INTO roles(name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- Authorities
INSERT INTO authorities (name) VALUES ('VIEW_TRANSLATIONS'),
('VIEW_LANGUAGES'),
('MANAGE_TRANSLATIONS'),
('MANAGE_LANGUAGES');


-- Customers
INSERT INTO public.customers
(email, pwd)
VALUES('maleeha@gmail.com', '{bcrypt}$2a$12$I6xD317/i257UUxFLIFQJOubHMtcXw8l30OZfOALuDhVZL8WseKJK'); -- Password: Nope123@

INSERT INTO public.customers
(email, pwd)
VALUES('maryam@gmail.com', '{noop}Nope123@');

-- Assign roles to users
INSERT INTO customer_roles (customer_id, role_id) VALUES
(1, 1), -- ROLE_USER
(2, 2); -- ROLE_ADMIN

-- Assign authorities to roles
-- ROLE_USER -> VIEW_ACCOUNT, VIEW_CARDS
INSERT INTO role_authorities (role_id, authority_id) VALUES (2, 1), (2, 2), (2, 3), (2, 4), (1, 3), (1, 4);


-- Language and Translation setup

-- languages
CREATE TABLE languages (
  code VARCHAR(10) PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

-- translations
CREATE TABLE translations (
  id BIGSERIAL PRIMARY KEY,
  translation_key VARCHAR(255) NOT NULL,
  lang_code VARCHAR(10) NOT NULL REFERENCES languages(code),
  text TEXT NOT NULL,
  tags TEXT[] DEFAULT '{}',
  UNIQUE(translation_key, lang_code)
);

-- Index for fast tag search
CREATE INDEX idx_translations_lang ON translations(lang_code);
CREATE INDEX idx_translations_tags ON translations USING gin (tags);
CREATE INDEX idx_translations_key ON translations(translation_key);
CREATE INDEX idx_translations_text ON translations USING gin (to_tsvector('simple', text));

