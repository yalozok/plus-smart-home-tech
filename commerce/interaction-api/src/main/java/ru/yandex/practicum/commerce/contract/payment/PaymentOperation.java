package ru.yandex.practicum.commerce.contract.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.contract.order.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.contract.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@RequestMapping("/api/v1/payment")
public interface PaymentOperation {
    @PostMapping
    PaymentDto calculatePayment(@RequestBody @NotNull @Valid OrderDto orderDto)
            throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@RequestBody @NotNull @Valid OrderDto orderDto)
            throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/refund")
    void confirmPayment(@RequestParam @NotBlank UUID paymentId)
            throws NoOrderFoundException;

    @PostMapping("/productCost")
    BigDecimal calculateProductCost(@RequestBody @NotNull @Valid OrderDto orderDto)
            throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/failed")
    BigDecimal failPayment(@RequestParam @NotBlank UUID paymentId)
            throws NoOrderFoundException;

}
