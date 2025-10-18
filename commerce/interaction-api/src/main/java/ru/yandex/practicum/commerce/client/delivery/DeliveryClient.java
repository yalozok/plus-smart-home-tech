package ru.yandex.practicum.commerce.client.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.delivery.DeliveryOperation;

@FeignClient(name = "delivery",
        path = "/api/v1/delivery",
        configuration = FeignConfig.class)
public interface DeliveryClient extends DeliveryOperation {
}
