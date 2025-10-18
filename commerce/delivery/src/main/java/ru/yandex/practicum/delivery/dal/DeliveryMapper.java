package ru.yandex.practicum.delivery.dal;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;

@Mapper(componentModel = "spring",
        uses = AddressMapper.class,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface DeliveryMapper {
    DeliveryDto toDto(Delivery delivery);

    Delivery toEntity(DeliveryDto deliveryDto);
}
