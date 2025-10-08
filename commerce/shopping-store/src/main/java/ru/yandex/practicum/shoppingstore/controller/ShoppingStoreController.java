package ru.yandex.practicum.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperation;
import ru.yandex.practicum.commerce.contract.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.dto.PageableDto;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.QuantityState;
import ru.yandex.practicum.commerce.request.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.shoppingstore.dal.PageableMapper;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.Set;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreOperation {
    private final ShoppingStoreService service;
    private final PageableMapper pageableMapper;

    @Override
    @Loggable
    public Page<ProductDto> getProducts(ProductCategory category,
                                        int page, int size, String[] sort) {
        PageableDto pageableDto = new PageableDto();
        pageableDto.setPage(page);
        pageableDto.setSize(size);
        pageableDto.setSort(sort != null ? sort : new String[]{"productName", "ASC"});

        return service.getProducts(category, pageableMapper.toSpringPageable(pageableDto));
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @Loggable
    public ProductDto addProduct(ProductDto productDto) {
        return service.addProduct(productDto);
    }

    @Override
    @Loggable
    public ProductDto updateProduct(ProductDto product) throws ProductNotFoundException {
        return service.updateProduct(product);
    }

    @Override
    @Loggable
    public boolean deleteProduct(UUID productId) throws ProductNotFoundException {
        return service.deactivateProduct(productId);
    }

    @Override
    @Loggable
    public boolean setProductQuantityState(UUID productId,
                                           QuantityState quantityState)
            throws ProductNotFoundException {
        SetProductQuantityStateRequest newQuantity = new SetProductQuantityStateRequest();
        newQuantity.setProductId(productId);
        newQuantity.setQuantityState(quantityState);
        return service.setProductQuantityState(newQuantity);
    }

    @Override
    @Loggable
    public ProductDto getProduct(UUID productId) throws ProductNotFoundException {
        return service.getProduct(productId);
    }

    @Override
    @Loggable
    public List<ProductDto> getProductsByIds(Set<UUID> productIds) {
        return service.getProductsByIds(productIds);
    }
}