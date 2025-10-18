package ru.yandex.practicum.warehouse.dal;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "warehouse_shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseShipment {
    @Id
    @UuidGenerator
    @Column(name = "shipment_id")
    private UUID shipmentId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @ElementCollection
    @CollectionTable(
            name = "warehouse_shipment_items",
            joinColumns = @JoinColumn(name = "shipment_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;
}
