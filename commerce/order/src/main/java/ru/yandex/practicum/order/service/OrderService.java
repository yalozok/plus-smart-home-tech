package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.client.delivery.DeliveryClient;
import ru.yandex.practicum.commerce.client.payment.PaymentClient;
import ru.yandex.practicum.commerce.client.warehouse.WareHouseClient;
import ru.yandex.practicum.commerce.contract.order.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.request.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.request.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.request.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.order.dal.Order;
import ru.yandex.practicum.order.dal.OrderMapper;
import ru.yandex.practicum.order.dal.OrderRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final WareHouseClient wareHouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    public Page<OrderDto> getOrdersByUser(String username, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUsername(username, pageable);
        return orders.map(orderMapper::toDto);
    }

    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest orderRequest) {
        Order newOrder = new Order();
        newOrder.setUsername(orderRequest.getUsername());
        newOrder.setShoppingCartId(orderRequest.getShoppingCart().getId());
        newOrder.setOrderState(OrderState.NEW);
        Order savedOrder = orderRepository.saveAndFlush(newOrder);

        AssemblyProductsForOrderRequest requestAssembly = new AssemblyProductsForOrderRequest(
                orderRequest.getShoppingCart().getProducts(),
                savedOrder.getOrderId()
        );
        BookedProductsDto bookedProducts = wareHouseClient.assemblyProductsForOrder(requestAssembly);
        savedOrder.setProducts(orderRequest.getShoppingCart().getProducts());
        savedOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        savedOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        savedOrder.setFragile(bookedProducts.getFragile());

        UUID deliveryId = initiateDelivery(savedOrder.getOrderId(), orderRequest.getDeliveryAddress());
        savedOrder.setDeliveryId(deliveryId);

        return orderMapper.toDto(orderRepository.save(savedOrder));
    }

    private UUID initiateDelivery(UUID orderId, AddressDto deliveryAddress) {
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setFrom(deliveryAddress);
        deliveryDto.setTo(wareHouseClient.getAddress());
        deliveryDto.setOrderId(orderId);
        deliveryDto.setDeliveryState(DeliveryState.CREATED);
        DeliveryDto deliverySaved = deliveryClient.createDelivery(deliveryDto);
        return deliverySaved.getDeliveryId();
    }

    @Transactional
    public OrderDto deliveryInProcess(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.ON_DELIVERY);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto deliverySuccess(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.DELIVERED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto initiatePayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        PaymentDto payment = paymentClient.setPayment(orderMapper.toDto(order));
        order.setPaymentId(payment.getPaymentId());
        order.setOrderState(OrderState.ON_PAYMENT);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto calculateOrderTotal(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));

        BigDecimal productCost = paymentClient.getProductCost(orderMapper.toDto(order));
        order.setProductPrice(productCost);

        BigDecimal totalCost = paymentClient.getTotalCost(orderMapper.toDto(order));
        order.setTotalPrice(totalCost);
        orderRepository.saveAndFlush(order);
        return orderMapper.toDto(order);
    }

    public OrderDto calculateOrderDelivery(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order " + orderId + " not found"));

        BigDecimal deliveryCost = deliveryClient.calculateDeliveryCost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto paymentSuccess(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.PAID);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order " + orderId + " not found"));
        order.setOrderState(OrderState.ASSEMBLED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.COMPLETED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        Order order = orderRepository.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        wareHouseClient.returnProductsToWarehouse(returnRequest.getProducts());
        order.setOrderState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.CANCELED);
        return orderMapper.toDto(orderRepository.save(order));
    }
}
