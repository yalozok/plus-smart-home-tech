package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.contract.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.request.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.request.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.WarehouseApp;
import ru.yandex.practicum.warehouse.dal.WarehouseProduct;
import ru.yandex.practicum.warehouse.dal.WarehouseProductRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseProductRepository warehouseProductRepository;

    @Transactional
    public void setProductToWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseProductRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Product " + request.getProductId() + " already exist in warehouse");
        }

        WarehouseProduct product = new WarehouseProduct(
                request.getProductId(),
                request.getFragile(),
                request.getDimension().getWidth(),
                request.getDimension().getHeight(),
                request.getDimension().getDepth(),
                request.getWeight(),
                0
        );
        warehouseProductRepository.save(product);
    }

    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseProductRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Product " + request.getProductId() + " not found in warehouse"));
        long newQuantity = product.getQuantity() + request.getQuantity();
        product.setQuantity(newQuantity);
    }

    public AddressDto getAddress() {
        String CURRENT_ADDRESS = WarehouseApp.getCurrentAddress();
        return new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS
        );
    }

    @Transactional
    public BookedProductsDto checkedBookedProducts(ShoppingCartDto cart) {
        BookedProductsDto bookedProducts = new BookedProductsDto();
        Map<UUID, Long> products = cart.getProducts();
        List<WarehouseProduct> productsInWarehouse = warehouseProductRepository.findAllById(products.keySet());
        productsInWarehouse.forEach(product -> {
            long requestedQuantity = products.get(product.getProductId());
            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Product " + product.getProductId() + " not enough in warehouse");
            }
            double productDeliveryWeight = product.getWeight() * requestedQuantity;
            double productDeliveryVolume = product.getDepth() * product.getHeight() * product.getWidth() * requestedQuantity;

            bookedProducts.setDeliveryWeight(bookedProducts.getDeliveryWeight() + productDeliveryWeight);
            bookedProducts.setDeliveryVolume(bookedProducts.getDeliveryVolume() + productDeliveryVolume);

            if (product.isFragile()) {
                bookedProducts.setFragile(true);
            }
        });
        return bookedProducts;
    }
}
