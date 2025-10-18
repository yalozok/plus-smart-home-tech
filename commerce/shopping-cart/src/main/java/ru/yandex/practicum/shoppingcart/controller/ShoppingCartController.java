package ru.yandex.practicum.shoppingcart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperation;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.request.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartOperation {
    private final ShoppingCartService service;

    @Override
    @Loggable
    public ShoppingCartDto getOrCreateShoppingCart(@RequestParam @NotEmpty String username) {
        return service.getOrCreateShoppingCart(username);
    }

    @Override
    @Loggable
    public ShoppingCartDto addProductToShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull Map<@NotNull UUID, @NotNull @Positive Long> products
    ) {
        return service.addProductToShoppingCart(username, products);
    }

    @Override
    @Loggable
    public void deactivateShoppingCart(@RequestParam @NotEmpty String username)
            throws NotAuthorizedUserException {
        service.deactivateShoppingCart(username);
    }

    @Override
    @Loggable
    public ShoppingCartDto removeProductFromShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull List<@NotNull UUID> productIds
    ) throws NotAuthorizedUserException, NoProductsInShoppingCartException {
        return service.removeProductFromShoppingCart(username, productIds);
    }

    @Override
    @Loggable
    public ShoppingCartDto changeProductQuantityInShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull ChangeProductQuantityRequest productQuantity
    ) throws NotAuthorizedUserException, NoProductsInShoppingCartException {
        return service.changeProductQuantityInShoppingCart(username, productQuantity);
    }
}
