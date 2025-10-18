package ru.yandex.practicum.warehouse.dal;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseShipmentRepository extends JpaRepository<WarehouseShipment, UUID> {
    Optional<WarehouseShipment> findByOrderId(@NotNull UUID orderId);
}
