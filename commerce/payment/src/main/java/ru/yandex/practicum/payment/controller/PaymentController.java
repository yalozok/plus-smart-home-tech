package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.payment.PaymentOperation;
import ru.yandex.practicum.commerce.contract.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.contract.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentOperation {
    private final PaymentService service;

    @Override
    @Loggable
    public BigDecimal getProductCost(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        return service.getProductCost(orderDto);
    }

    @Override
    @Loggable
    public BigDecimal getTotalCost(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        return service.getTotalCost(orderDto);
    }

    @Override
    @Loggable
    public PaymentDto setPayment(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        return service.setPayment(orderDto);
    }

    @Override
    @Loggable
    public void paymentSuccess(UUID paymentId) throws NoPaymentFoundException {
        service.paymentSuccess(paymentId);
    }

    @Override
    @Loggable
    public void paymentFailed(UUID paymentId) throws NoPaymentFoundException {
        service.paymentFailed(paymentId);
    }
}
