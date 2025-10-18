package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.yandex.practicum.warehouse.WarehouseApp;
import ru.yandex.practicum.warehouse.dal.WarehouseProduct;
import ru.yandex.practicum.warehouse.dal.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.dal.WarehouseShipment;
import ru.yandex.practicum.warehouse.dal.WarehouseShipmentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseProductRepository productRepository;
    private final WarehouseShipmentRepository shipmentRepository;

    @Transactional
    public void setProductToWarehouse(NewProductInWarehouseRequest request) {
        if (productRepository.existsById(request.getProductId())) {
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
        productRepository.save(product);
    }

    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Product " + request.getProductId() + " not found in warehouse"));
        long newQuantity = product.getQuantity() + request.getQuantity();
        product.setQuantity(newQuantity);
        productRepository.save(product);
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
        Map<UUID, Long> requestedProducts = cart.getProducts();
        List<WarehouseProduct> productsInWarehouse = getProductsInWarehouseByIds(requestedProducts.keySet());
        validateStockAvailability(requestedProducts, productsInWarehouse);
        return calculateBookedProductsTotalDimensions(requestedProducts, productsInWarehouse);
    }

    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        WarehouseShipment shipment = shipmentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new NoShipmentInWarehouseFoundException(
                        "Shipment by order" + request.getOrderId() + " not found in warehouse"));

        shipment.setDeliveryId(request.getDeliveryId());
        shipmentRepository.save(shipment);
    }

    @Transactional
    public void returnProductsToWarehouse(Map<UUID, Long> products) {
        List<WarehouseProduct> productsInWarehouse = getProductsInWarehouseByIds(products.keySet());
        productsInWarehouse.forEach(product -> {
            long returnedQuantity = products.get(product.getProductId());
            product.setQuantity(product.getQuantity() + returnedQuantity);
        });
        productRepository.saveAll(productsInWarehouse);
    }

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> requestedProducts = request.getProducts();
        List<WarehouseProduct> productsInWarehouse = getProductsInWarehouseByIds(requestedProducts.keySet());
        validateStockAvailability(requestedProducts, productsInWarehouse);

        productsInWarehouse.forEach(product -> {
            long requestedQuantity = requestedProducts.get(product.getProductId());
            product.setQuantity(Math.max(product.getQuantity() - requestedQuantity, 0));
        });
        productRepository.saveAllAndFlush(productsInWarehouse);

        WarehouseShipment shipment = new WarehouseShipment();
        shipment.setOrderId(request.getOrderId());
        shipment.setProducts(request.getProducts());
        shipmentRepository.save(shipment);

        return calculateBookedProductsTotalDimensions(requestedProducts, productsInWarehouse);
    }

    private List<WarehouseProduct> getProductsInWarehouseByIds(Set<UUID> productIds) {
        List<WarehouseProduct> productsInWarehouse = productRepository.findAllById(productIds);

        if (productIds.size() != productsInWarehouse.size()) {
            Set<UUID> foundIds = productsInWarehouse.stream()
                    .map(WarehouseProduct::getProductId)
                    .collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(productIds);
            missingIds.removeAll(foundIds);

            throw new NoSpecifiedProductInWarehouseException(
                    "Products not found in warehouse: " + missingIds
            );
        }
        return productsInWarehouse;
    }

    private void validateStockAvailability(Map<UUID, Long> requestedProducts, List<WarehouseProduct> products) {
        products.forEach(product -> {
            long requestedQuantity = requestedProducts.get(product.getProductId());
            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Product " + product.getProductId() + " not enough in warehouse");
            }
        });
    }

    private BookedProductsDto calculateBookedProductsTotalDimensions(Map<UUID, Long> requestedProducts, List<WarehouseProduct> products) {
        BookedProductsDto bookedProducts = new BookedProductsDto();
        products.forEach(product -> {
            long requestedQuantity = requestedProducts.get(product.getProductId());
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
