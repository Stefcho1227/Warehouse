CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              item_name VARCHAR(90) NOT NULL,
                              quantity NUMERIC(14,3) NOT NULL,
                              unit TEXT NOT NULL,
                              unit_price NUMERIC(10,2) NOT NULL,
                              warehouse_name VARCHAR(90) NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
CREATE TABLE inventory (
                           id BIGSERIAL PRIMARY KEY,
                           item_name VARCHAR(90) NOT NULL,
                           warehouse_name VARCHAR(90) NOT NULL,
                           unit_price NUMERIC(10,2) NOT NULL,
                           unit TEXT NOT NULL,
                           quantity NUMERIC(14,3) NOT NULL DEFAULT 0,
                           CONSTRAINT uq_inventory UNIQUE (item_name, warehouse_name, unit_price)
);
CREATE INDEX idx_transactions_item ON transactions(item_name);
CREATE INDEX idx_transactions_warehouse ON transactions(warehouse_name);