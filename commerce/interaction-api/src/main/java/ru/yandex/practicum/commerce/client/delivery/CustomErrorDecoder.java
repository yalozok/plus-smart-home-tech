package ru.yandex.practicum.commerce.client.delivery;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import ru.yandex.practicum.commerce.contract.delivery.exception.NoDeliveryFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = extractMessage(response);
        if (response.status() == 400) {
            return new NoDeliveryFoundException(message);
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
