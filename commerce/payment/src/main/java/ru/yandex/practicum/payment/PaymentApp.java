package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum.payment",
        "ru.yandex.practicum.logging"
})
@EnableFeignClients(basePackages = {
        "ru.yandex.practicum.commerce.client"
})
@EnableAspectJAutoProxy
public class PaymentApp {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }

}
