package ru.yandex.practicum.delivery.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByCountryAndCityAndStreetAndHouseAndFlat(
            String country, String city, String street, String house, String flat);
}
