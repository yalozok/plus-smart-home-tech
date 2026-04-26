package ru.yandex.practicum.delivery.dal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @UuidGenerator
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "order_id")
    private UUID orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_from_id")
    private Address from;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_to_id")
    private Address to;

    @Column(name = "delivery_state")
    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;

    @Column(name = "total_weight")
    private double totalWeight;

    @Column(name = "total_volume")
    private double totalVolume;

    @Column(name = "fragile")
    private boolean fragile = false;
}
