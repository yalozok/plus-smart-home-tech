package ru.yandex.practicum.commerce.contract.shopping.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(String userId) {
        super("User " + userId + " is not authorized");
    }
}
