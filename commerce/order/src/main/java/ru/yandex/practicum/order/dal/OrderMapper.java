package ru.yandex.practicum.order.dal;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    OrderDto toDto(Order order);

    Order toEntity(OrderDto orderDto);
}
