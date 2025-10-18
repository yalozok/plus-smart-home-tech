package ru.yandex.practicum.commerce.contract.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.contract.order.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.request.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.request.order.ProductReturnRequest;

import java.util.UUID;

public interface OrderOperation {
    @GetMapping
    Page<OrderDto> getOrdersByUser(@RequestParam @NotBlank String username,
                                   @RequestParam(required = false) int page,
                                   @RequestParam(required = false) int size,
                                   @RequestParam(required = false) String[] sort)
            throws NotAuthorizedUserException;

    @PutMapping
    OrderDto createOrder(@RequestBody @NotNull @Valid CreateNewOrderRequest newOrder)
            throws NoSpecifiedProductInWarehouseException;

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody @NotNull @Valid ProductReturnRequest returnRequest)
            throws NoOrderFoundException;

    @PostMapping("/payment")
    OrderDto initiatePayment(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/payment/success")
    OrderDto paymentSuccess(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/delivery")
    OrderDto deliveryInProcess(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/delivery/success")
    OrderDto deliverySuccess(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/calculate/total")
    OrderDto calculateOrderTotal(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateOrderDelivery(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/assembly")
    OrderDto assembly(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestParam @NotNull UUID orderId)
            throws NoOrderFoundException;

}
