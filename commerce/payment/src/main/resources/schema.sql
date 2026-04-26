CREATE TABLE IF NOT EXISTS payments(
    payment_id UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    order_id UUID NOT NULL,
    total_payment NUMERIC(19,2) NOT NULL,
    delivery_total NUMERIC(19,2) NOT NULL,
    product_total NUMERIC(19,2) NOT NULL,
    fee_total NUMERIC(19,2) NOT NULL,
    payment_state VARCHAR(10) NOT NULL,
    CONSTRAINT unique_order_id UNIQUE (order_id),
    CONSTRAINT payment_state_check CHECK (payment_state IN ('PENDING', 'SUCCESS', 'FAILED'))
);