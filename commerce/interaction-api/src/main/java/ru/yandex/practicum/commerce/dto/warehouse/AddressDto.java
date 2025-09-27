package ru.yandex.practicum.commerce.dto.warehouse;

import lombok.Data;

@Data
public class AddressDto {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}
