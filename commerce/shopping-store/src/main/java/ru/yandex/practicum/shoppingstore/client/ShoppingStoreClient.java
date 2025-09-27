package ru.yandex.practicum.shoppingstore.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperation;

@FeignClient(name = "shopping-store",
        path = "/api/v1/shopping-store",
        configuration = FeignConfig.class)
public interface ShoppingStoreClient extends ShoppingStoreOperation {
}
