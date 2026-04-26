CREATE TABLE IF NOT EXISTS warehouse_products(
    product_id uuid PRIMARY KEY NOT NULL,
    fragile BOOLEAN,
    width DOUBLE PRECISION NOT NULL CHECK ( width >= 1 ),
    height DOUBLE PRECISION NOT NULL CHECK ( height >= 1 ),
    depth DOUBLE PRECISION NOT NULL CHECK ( depth >= 1 ),
    weight DOUBLE PRECISION NOT NULL CHECK ( weight >= 1 ),
    quantity BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT unique_product UNIQUE (product_id)
);

CREATE TABLE IF NOT EXISTS warehouse_shipments(
    shipment_id uuid DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
    order_id uuid NOT NULL,
    delivery_id uuid
);

CREATE TABLE IF NOT EXISTS warehouse_shipment_items(
    shipment_id uuid NOT NULL,
    product_id uuid NOT NULL,
    quantity INT NOT NULL CHECK ( quantity > 0 ),
    PRIMARY KEY (shipment_id, product_id),
    constraint fk_shipment_id
       FOREIGN KEY (shipment_id)
       REFERENCES warehouse_shipments(shipment_id) ON DELETE CASCADE
);