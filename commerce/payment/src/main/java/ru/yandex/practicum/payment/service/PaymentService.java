package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.client.order.OrderClient;
import ru.yandex.practicum.commerce.client.shopping.store.ShoppingStoreClient;
import ru.yandex.practicum.commerce.contract.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.contract.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.contract.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.payment.dal.Payment;
import ru.yandex.practicum.payment.dal.PaymentMapper;
import ru.yandex.practicum.payment.dal.PaymentRepository;
import ru.yandex.practicum.payment.dal.PaymentState;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ShoppingStoreClient storeClient;
    private final OrderClient orderClient;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private static final BigDecimal TEN_PERCENT = new BigDecimal("0.10");

    public BigDecimal getProductCost(OrderDto orderDto) {
        Map<UUID, Long> productsFromOrder = orderDto.getProducts();
        List<ProductDto> products;

        try {
            products = storeClient.getProductsByIds(productsFromOrder.keySet());
        } catch (ProductNotFoundException e) {
            throw new NotEnoughInfoInOrderToCalculateException("Products not found in store: " + e.getMessage());
        }

        return products.stream()
                .map(product -> product.getPrice()
                        .multiply(BigDecimal.valueOf(productsFromOrder.getOrDefault(product.getProductId(), 0L))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCost(OrderDto orderDto) {
        if(orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Delivery price not found");
        }
        if(orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Product price not found");
        }
        BigDecimal productCost = orderDto.getProductPrice().multiply(TEN_PERCENT);
        BigDecimal deliveryCost = orderDto.getDeliveryPrice();
        return productCost.add(deliveryCost);
    }

    public PaymentDto setPayment(OrderDto orderDto) {
        if(orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Delivery price not found");
        }
        if(orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Product price not found");
        }
        if(orderDto.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Total price not found");
        }

        Payment payment = new Payment();
        payment.setTotalPayment(orderDto.getTotalPrice());
        payment.setDeliveryTotal(orderDto.getDeliveryPrice());
        payment.setProductTotal(orderDto.getProductPrice());
        payment.setFeeTotal(orderDto.getProductPrice().multiply(TEN_PERCENT)
                .subtract(orderDto.getDeliveryPrice()));
        payment.setPaymentState(PaymentState.PENDING);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public void paymentSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("Payment " + paymentId + " not found")
        );
        payment.setPaymentState(PaymentState.SUCCESS);
        paymentRepository.save(payment);
        orderClient.paymentSuccess(payment.getOrderId());
    }

    public void paymentFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("Payment " + paymentId + " not found")
        );
        payment.setPaymentState(PaymentState.FAILED);
        paymentRepository.save(payment);
        orderClient.paymentFailed(payment.getOrderId());
    }
}
