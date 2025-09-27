package ru.yandex.practicum.commerce.contract.shopping.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoProductsInShoppingCartException extends RuntimeException {
    public NoProductsInShoppingCartException(UUID cartId) {
        super("No products in cart " + cartId);
    }
}
