package ru.yandex.practicum.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum.delivery",
        "ru.yandex.practicum.logging"
})
@EnableFeignClients(basePackages = {
        "ru.yandex.practicum.commerce.client"
})
@EnableAspectJAutoProxy
public class DeliveryApp {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryApp.class, args);
    }

}
