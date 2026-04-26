package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperation;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoShipmentInWarehouseFoundException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.contract.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.request.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.request.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.request.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.request.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseOperation {
    private final WarehouseService service;

    @Override
    @Loggable
    public void setProductToWarehouse(@RequestBody @Valid @NotNull NewProductInWarehouseRequest request)
            throws SpecifiedProductAlreadyInWarehouseException {
        service.setProductToWarehouse(request);
    }

    @Override
    @Loggable
    public BookedProductsDto checkBookedProducts(@RequestBody @Valid @NotNull ShoppingCartDto cart)
            throws ProductInShoppingCartLowQuantityInWarehouse, NoSpecifiedProductInWarehouseException {
        return service.checkedBookedProducts(cart);
    }

    @Override
    @Loggable
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToWarehouse(AddProductToWarehouseRequest request)
            throws NoSpecifiedProductInWarehouseException {
        service.addProductToWarehouse(request);
    }

    @Override
    @Loggable
    public AddressDto getAddress() {
        return service.getAddress();
    }

    @Override
    @Loggable
    public void shippedToDelivery(ShippedToDeliveryRequest shipRequest)
            throws NoShipmentInWarehouseFoundException {
        service.shippedToDelivery(shipRequest);
    }

    @Override
    @Loggable
    public void returnProductsToWarehouse(Map<UUID, Long> products)
            throws NoSpecifiedProductInWarehouseException {
        service.returnProductsToWarehouse(products);
    }

    @Override
    @Loggable
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request)
            throws NoSpecifiedProductInWarehouseException, ProductInShoppingCartLowQuantityInWarehouse {
        return service.assemblyProductsForOrder(request);
    }
}
