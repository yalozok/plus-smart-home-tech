package ru.yandex.practicum.commerce.client.order;


import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.order.OrderOperation;

@FeignClient(name = "order",
        path = "/api/v1/order",
        configuration = FeignConfig.class)
public interface OrderClient extends OrderOperation {
}
