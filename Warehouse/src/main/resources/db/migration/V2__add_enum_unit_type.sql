CREATE TYPE unit_type AS ENUM ('KG', 'PCS', 'L');
ALTER TABLE transactions
ALTER COLUMN unit TYPE unit_type USING unit::unit_type;

ALTER TABLE inventory
ALTER COLUMN unit TYPE unit_type USING unit::unit_type;