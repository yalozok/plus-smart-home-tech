package ru.yandex.practicum.commerce.client.warehouse;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
}
