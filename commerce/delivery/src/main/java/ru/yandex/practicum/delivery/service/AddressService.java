package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.delivery.dal.Address;
import ru.yandex.practicum.delivery.dal.AddressMapper;
import ru.yandex.practicum.delivery.dal.AddressRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Transactional
    public Address getorCreateAddress(AddressDto addressDto) {
        return addressRepository.findByCountryAndCityAndStreetAndHouseAndFlat(
                addressDto.getCountry(),
                addressDto.getCity(),
                addressDto.getStreet(),
                addressDto.getHouse(),
                addressDto.getFlat()
        ).orElseGet(() -> {
            Address address = addressMapper.toEntity(addressDto);
            return addressRepository.saveAndFlush(address);
        });
    }
}
