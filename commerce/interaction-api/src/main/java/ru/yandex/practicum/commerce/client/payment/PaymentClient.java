package ru.yandex.practicum.commerce.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.payment.PaymentOperation;

@FeignClient(name = "payment",
        path = "/api/v1/payment",
        configuration = FeignConfig.class)
public interface PaymentClient extends PaymentOperation {
}
