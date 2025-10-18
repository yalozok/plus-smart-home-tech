CREATE TABLE IF NOT EXISTS addresses(
    address_id UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    country VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    street VARCHAR(50) NOT NULL,
    house VARCHAR(50) NOT NULL,
    flat VARCHAR(50) NOT NULL,
    CONSTRAINT unique_address UNIQUE (country, city, street, house, flat)
);
DROP TABLE IF EXISTS deliveries;
CREATE TABLE IF NOT EXISTS deliveries(
    delivery_id UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    order_id UUID NOT NULL,
    address_from_id UUID NOT NULL,
    address_to_id UUID NOT NULL,
    delivery_state VARCHAR(10) NOT NULL,
    total_weight DOUBLE PRECISION,
    total_Volume DOUBLE PRECISION,
    fragile BOOLEAN,
    CONSTRAINT delivery_state_check
        CHECK (delivery_state IN ('CREATED', 'IN_PROGRESS', 'DELIVERED', 'FAILED', 'CANCELED')),
    CONSTRAINT unique_order_id UNIQUE (order_id)
);