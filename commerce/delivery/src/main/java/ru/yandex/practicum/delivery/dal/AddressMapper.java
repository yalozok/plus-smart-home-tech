package ru.yandex.practicum.delivery.dal;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.AddressDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AddressMapper {
    AddressDto toDto(Address address);

    Address toEntity(AddressDto addressDto);
}
