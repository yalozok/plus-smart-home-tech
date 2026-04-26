package ru.yandex.practicum.commerce.contract.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.contract.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.contract.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperation {
    @PostMapping("/productCost")
    BigDecimal getProductCost(@RequestBody @NotNull @Valid OrderDto orderDto)
            throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@RequestBody @NotNull @Valid OrderDto orderDto)
            throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping
    PaymentDto setPayment(@RequestBody @NotNull @Valid OrderDto orderDto)
            throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/refund")
    void paymentSuccess(@RequestParam @NotBlank UUID paymentId)
            throws NoPaymentFoundException;

    @PostMapping("/failed")
    void paymentFailed(@RequestParam @NotBlank UUID paymentId)
            throws NoPaymentFoundException;

}
