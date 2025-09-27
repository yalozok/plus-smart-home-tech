package ru.yandex.practicum.shoppingstore.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperation;
import ru.yandex.practicum.commerce.contract.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.dto.shopping.store.PageableDto;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.QuantityState;
import ru.yandex.practicum.commerce.request.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.shoppingstore.dal.PageableMapper;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreController implements ShoppingStoreOperation {
    private final ShoppingStoreService service;
    private final PageableMapper pageableMapper;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        @RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "10") int size,
                                        @RequestParam(required = false) String[] sort) {
        log.info("==> getProducts: category={}, page={}, size={}, sort={}", category, page, size, sort);

        PageableDto pageableDto = new PageableDto();
        pageableDto.setPage(page);
        pageableDto.setSize(size);
        pageableDto.setSort(sort != null ? sort : new String[]{"productName", "ASC"});

        Page<ProductDto> products = service.getProducts(category, pageableMapper.toSpringPageable(pageableDto));
        log.info("<== getProducts: products.size={}", products.getSize());
        return products;
    }

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto addProduct(@RequestBody ProductDto productDto) {
        log.info("==> addProduct: product={}", productDto);
        ProductDto savedProduct = service.addProduct(productDto);
        log.info("<== addProduct: savedProduct={}", savedProduct);
        return savedProduct;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@RequestBody ProductDto product) throws ProductNotFoundException {
        log.info("==> updateProduct: product={}", product);
        ProductDto updatedProduct = service.updateProduct(product);
        log.info("<== updateProduct: updatedProduct={}", updatedProduct);
        return updatedProduct;
    }

    @Override
    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteProduct(@RequestBody UUID productId) throws ProductNotFoundException {
        log.info("==> deleteProduct: productId={}", productId);
        return service.deleteProduct(productId);
    }

    @Override
    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public boolean setProductQuantityState(@RequestParam @NotNull UUID productId,
                                           @RequestParam @NotNull QuantityState quantityState)
            throws ProductNotFoundException {
        log.info("==> setProductQuantityState: productId={}, quantityState={}", productId, quantityState);
        SetProductQuantityStateRequest newQuantity = new SetProductQuantityStateRequest();
        newQuantity.setProductId(productId);
        newQuantity.setQuantityState(quantityState);
        return service.setProductQuantityState(newQuantity);
    }

    @Override
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProduct(@PathVariable UUID productId) throws ProductNotFoundException {
        log.info("==> getProduct: productId={}", productId);
        ProductDto product = service.getProduct(productId);
        log.info("<== getProduct: product={}", product);
        return product;
    }
}