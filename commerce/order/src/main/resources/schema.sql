drop table if exists order_items;
drop table if exists orders;
CREATE TABLE IF NOT EXISTS orders(
    order_id uuid DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
    username VARCHAR(255) NOT NULL,
    shopping_cart_id uuid NOT NULL,
    payment_id uuid,
    delivery_id uuid,
    order_state VARCHAR(20) NOT NULL,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price NUMERIC(19,2),
    delivery_price NUMERIC(19,2),
    product_price NUMERIC(19,2),
    CONSTRAINT order_state_check CHECK ( order_state IN (
                'NEW', 'ON_PAYMENT', 'ON_DELIVERY', 'DONE', 'DELIVERED',
                'ASSEMBLED', 'PAID', 'COMPLETED', 'DELIVERY_FAILED',
                'ASSEMBLY_FAILED', 'PAYMENT_FAILED', 'PRODUCT_RETURNED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS order_items(
   order_id uuid NOT NULL,
   product_id uuid NOT NULL,
   quantity INT NOT NULL CHECK ( quantity > 0 ),
   PRIMARY KEY (order_id, product_id),
   constraint fk_order_id
       FOREIGN KEY (order_id)
           REFERENCES orders(order_id) ON DELETE CASCADE
);