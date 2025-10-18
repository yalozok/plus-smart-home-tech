package ru.yandex.practicum.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum.order",
        "ru.yandex.practicum.logging"
})
@EnableFeignClients(basePackages = {
        "ru.yandex.practicum.commerce.client"
})
@EnableAspectJAutoProxy
public class OrderApp {

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

}
