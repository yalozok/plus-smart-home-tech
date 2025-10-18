package ru.yandex.practicum.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.order.OrderOperation;
import ru.yandex.practicum.commerce.contract.order.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.dto.PageableDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.request.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.request.order.ProductReturnRequest;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.order.dal.PageableMapper;
import ru.yandex.practicum.order.service.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderOperation {
    private final OrderService orderService;
    private final PageableMapper pageableMapper;

    @Override
    @Loggable
    public Page<OrderDto> getOrdersByUser(String username, int page, int size, String[] sort) throws NoOrderFoundException {
        PageableDto pageableDto = new PageableDto();
        pageableDto.setPage(page);
        pageableDto.setSize(size);
        pageableDto.setSort(sort != null ? sort : new String[]{"productName", "ASC"});

        return orderService.getOrdersByUser(username, pageableMapper.toSpringPageable(pageableDto));
    }

    @Override
    @Loggable
    public OrderDto createOrder(CreateNewOrderRequest newOrder) throws NoSpecifiedProductInWarehouseException {
        return orderService.createOrder(newOrder);
    }

    @Override
    @Loggable
    public OrderDto returnOrder(ProductReturnRequest returnRequest) throws NoOrderFoundException {
        return orderService.returnOrder(returnRequest);
    }

    @Override
    @Loggable
    public OrderDto initiatePayment(UUID orderId) throws NoOrderFoundException {
        return orderService.initiatePayment(orderId);
    }

    @Override
    @Loggable
    public OrderDto paymentSuccess(UUID orderId) throws NoOrderFoundException {
        return orderService.paymentSuccess(orderId);
    }

    @Override
    @Loggable
    public OrderDto paymentFailed(UUID orderId) throws NoOrderFoundException {
        return orderService.paymentFailed(orderId);
    }

    @Override
    @Loggable
    public OrderDto deliveryInProcess(UUID orderId) throws NoOrderFoundException {
        return orderService.deliveryInProcess(orderId);
    }

    @Override
    @Loggable
    public OrderDto deliverySuccess(UUID orderId) throws NoOrderFoundException {
        return orderService.deliverySuccess(orderId);
    }

    @Override
    @Loggable
    public OrderDto deliveryFailed(UUID orderId) throws NoOrderFoundException {
        return orderService.deliveryFailed(orderId);
    }

    @Override
    @Loggable
    public OrderDto completeOrder(UUID orderId) throws NoOrderFoundException {
        return orderService.completeOrder(orderId);
    }

    @Override
    @Loggable
    public OrderDto calculateOrderTotal(UUID orderId) throws NoOrderFoundException {
        return orderService.calculateOrderTotal(orderId);
    }

    @Override
    @Loggable
    public OrderDto calculateOrderDelivery(UUID orderId) throws NoOrderFoundException {
        return orderService.calculateOrderDelivery(orderId);
    }

    @Override
    @Loggable
    public OrderDto assembly(UUID orderId) throws NoOrderFoundException {
        return orderService.assembly(orderId);
    }

    @Override
    @Loggable
    public OrderDto assemblyFailed(UUID orderId) throws NoOrderFoundException {
        return orderService.assemblyFailed(orderId);
    }
}
