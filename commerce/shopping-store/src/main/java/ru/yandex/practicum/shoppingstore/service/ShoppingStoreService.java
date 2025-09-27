package ru.yandex.practicum.shoppingstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductState;
import ru.yandex.practicum.commerce.request.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.shoppingstore.dal.Product;
import ru.yandex.practicum.shoppingstore.dal.ProductMapper;
import ru.yandex.practicum.shoppingstore.dal.ShoppingStoreRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreService {
    private final ShoppingStoreRepository repository;
    private final ProductMapper mapper;

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = repository.findByProductCategory(category, pageable);
        return products.map(mapper::toDto);
    }

    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        Product product = mapper.toEntity(productDto);
        return mapper.toDto(repository.save(product));
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = mapper.toEntity(productDto);
        return mapper.toDto(repository.save(product));
    }

    @Transactional
    public boolean deleteProduct(UUID productId) {
        Product product = repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product " + productId + " not found"));
        product.setProductState(ProductState.DEACTIVATE);
        Product savedProduct = repository.save(product);
        return savedProduct.getProductState() == ProductState.DEACTIVATE;
    }

    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        UUID productId = request.getProductId();
        Product product = repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product " + productId + " not found")
        );
        product.setQuantityState(request.getQuantityState());
        Product savedProduct = repository.save(product);
        return savedProduct.getQuantityState() == request.getQuantityState();
    }

    public ProductDto getProduct(UUID productId) {
        return mapper.toDto(repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product " + productId + " not found")
        ));
    }

}
