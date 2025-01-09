CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    variation_sku VARCHAR(255),
    ean VARCHAR(255),
    quantity INT NOT NULL,
    sale_price DECIMAL(10, 2),
    category_id BIGINT,
    expiry_date DATE,
    supplier_id BIGINT,
    weight DECIMAL(10, 2),
    images TEXT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    error TEXT,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE import_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Data e hora da importação
    status VARCHAR(255),                              -- Status da importação (ex: "sucesso", "falha", etc.)
    imported_products_count INT,                      -- Número de produtos importados
    error_details TEXT,                               -- Detalhes sobre erros (caso existam)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- Data de criação do registro
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Data de atualização do registro
);
