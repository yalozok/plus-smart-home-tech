package ru.yandex.practicum.shoppingcart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.client.warehouse.WareHouseClient;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.request.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.shoppingcart.dal.ShoppingCart;
import ru.yandex.practicum.shoppingcart.dal.ShoppingCartMapper;
import ru.yandex.practicum.shoppingcart.dal.ShoppingCartRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartService {
    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WareHouseClient warehouseClient;


    public ShoppingCartDto getOrCreateShoppingCart(String username) {
        ShoppingCart cart = findShoppingCartByUseOrCreateNewOne(username);
        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        ShoppingCart cart = findShoppingCartByUseOrCreateNewOne(username);
        Map<UUID, Long> mergedProducts = new HashMap<>(cart.getProducts());
        products.forEach((productId, productQuantity) ->
                mergedProducts.merge(productId, productQuantity, Long::sum));

        ShoppingCartDto proposedCart = mapper.toDto(cart);
        proposedCart.setProducts(mergedProducts);
        warehouseClient.checkBookedProducts(proposedCart);

        cart.setProducts(mergedProducts);
        return mapper.toDto(repository.save(cart));
    }

    private ShoppingCart findShoppingCartByUseOrCreateNewOne(String username) {
        return repository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUsername(username);
                    newCart.setActive(true);
                    newCart.setCreatedAt(LocalDateTime.now());
                    return repository.save(newCart);
                });
    }

    @Transactional
    public void deactivateShoppingCart(String username) {
        ShoppingCart cart = repository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NotAuthorizedUserException("User " + username + " not found"));
        cart.setActive(false);
        repository.save(cart);
    }

    @Transactional
    public ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> productIds) {
        ShoppingCart cart = repository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NotAuthorizedUserException("User " + username + " not found"));
        List<UUID> notFoundProducts = productIds.stream()
                .filter(id -> !cart.getProducts().containsKey(id))
                .toList();
        if (!notFoundProducts.isEmpty()) {
            throw new NoProductsInShoppingCartException("Products " + notFoundProducts + " not found in cart");
        }

        productIds.forEach((id) -> cart.getProducts().remove(id));
        return mapper.toDto(repository.save(cart));
    }

    @Transactional
    public ShoppingCartDto changeProductQuantityInShoppingCart(String username, ChangeProductQuantityRequest productQuantity) {
        ShoppingCart cart = repository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NotAuthorizedUserException("User " + username + " not found"));
        if (!cart.getProducts().containsKey(productQuantity.getProductId())) {
            throw new NoProductsInShoppingCartException("Product " + productQuantity.getProductId() + " not found in cart");
        }
        cart.getProducts().put(productQuantity.getProductId(), productQuantity.getNewQuantity());
        return mapper.toDto(repository.save(cart));
    }
}
