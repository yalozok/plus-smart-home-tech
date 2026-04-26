package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.client.order.OrderClient;
import ru.yandex.practicum.commerce.client.warehouse.WareHouseClient;
import ru.yandex.practicum.commerce.contract.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.request.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.delivery.dal.Address;
import ru.yandex.practicum.delivery.dal.Delivery;
import ru.yandex.practicum.delivery.dal.DeliveryMapper;
import ru.yandex.practicum.delivery.dal.DeliveryRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final AddressService addressService;
    private final OrderClient orderClient;
    private final WareHouseClient warehouseClient;

    private final BigDecimal BASE_DELIVERY_COST = BigDecimal.valueOf(5);
    private final BigDecimal ADDRESS1_DELIVERY_FACTOR = BigDecimal.valueOf(1);
    private final BigDecimal ADDRESS2_DELIVERY_FACTOR = BigDecimal.valueOf(2);

    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Address from = addressService.getorCreateAddress(deliveryDto.getFrom());
        Address to = addressService.getorCreateAddress(deliveryDto.getTo());
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setFrom(from);
        delivery.setTo(to);
        return deliveryMapper.toDto(deliveryRepository.saveAndFlush(delivery));
    }


    @Transactional
    public void successDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery " + deliveryId + " not found"));
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.deliverySuccess(delivery.getOrderId());
    }

    @Transactional
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery " + deliveryId + " not found"));
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderClient.deliveryFailed(delivery.getOrderId());
    }

    @Transactional
    public void pickDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery " + deliveryId + " not found"));
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        ShippedToDeliveryRequest shipRequest = new ShippedToDeliveryRequest(delivery.getOrderId(), deliveryId);
        warehouseClient.shippedToDelivery(shipRequest);
        orderClient.deliveryInProcess(delivery.getOrderId());
    }

    @Transactional
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId()).
                orElseThrow(() -> new NoDeliveryFoundException("Delivery " + orderDto.getDeliveryId() + " not found"));
        Address from = delivery.getFrom();
        Address to = delivery.getTo();

        BigDecimal totalCost = BASE_DELIVERY_COST;

        BigDecimal warehouseRemotenessFactor = switch (from.getStreet()) {
            case "ADDRESS_2" -> totalCost.multiply(ADDRESS2_DELIVERY_FACTOR);
            case "ADDRESS_1" -> totalCost.multiply(ADDRESS1_DELIVERY_FACTOR);
            default -> totalCost;
        };
        totalCost = totalCost.add(warehouseRemotenessFactor);

        if (orderDto.isFragile()) {
            totalCost = totalCost.add(
                    totalCost.multiply(BigDecimal.valueOf(0.2)));
        }

        totalCost = totalCost
                .add(BigDecimal.valueOf(orderDto.getDeliveryWeight() * 0.3))
                .add(BigDecimal.valueOf(orderDto.getDeliveryVolume() * 0.2));

        boolean isDifferentDestination =
                !Objects.equals(to.getCountry(), from.getCountry()) ||
                        !Objects.equals(to.getCity(), from.getCity()) ||
                        !Objects.equals(to.getStreet(), from.getStreet());

        if (isDifferentDestination) {
            totalCost = totalCost.add(totalCost.multiply(BigDecimal.valueOf(0.2)));
        }
        return totalCost;
    }
}
