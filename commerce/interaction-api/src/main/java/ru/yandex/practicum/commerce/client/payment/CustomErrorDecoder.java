package ru.yandex.practicum.commerce.client.payment;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import ru.yandex.practicum.commerce.contract.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.contract.payment.exception.NotEnoughInfoInOrderToCalculateException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = extractMessage(response);
        if (response.status() == 400) {
            return new NotEnoughInfoInOrderToCalculateException(message);
        } else if (response.status() == 404) {
            return new NoPaymentFoundException(message);
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }

    private String extractMessage(Response response) {
        if (response.body() == null) {
            return "No error message available";
        }

        try {
            return IOUtils.toString(response.body().asReader(StandardCharsets.UTF_8));
        } catch (IOException e) {
            return "Failed to extract error message from exception response";
        }
    }
}
