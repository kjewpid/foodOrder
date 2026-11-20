package se.yrgo.user_service.service;

import org.springframework.stereotype.Service;
import se.yrgo.user_service.dto.AddressDto;
import se.yrgo.user_service.dto.CreateAddressDto;
import se.yrgo.user_service.entity.Address;
import se.yrgo.user_service.entity.User;
import se.yrgo.user_service.exception.AddressNotFoundException;
import se.yrgo.user_service.exception.UserWithIdNotFoundException;
import se.yrgo.user_service.repository.AddressRepository;
import se.yrgo.user_service.repository.UserRepository;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<AddressDto> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    public AddressDto createAddress(Long userId, CreateAddressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserWithIdNotFoundException(userId));

        Address address = new Address();
        address.setStreetName(dto.getStreetName());
        address.setCity(dto.getCity());
        address.setZipCode(dto.getZipCode());
        address.setCountry(dto.getCountry());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return convertToDto(savedAddress);
    }


    public AddressDto updateAddress(Long addressId, CreateAddressDto dto) {
        Address oldAddress = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Address not found: " + addressId));

        oldAddress.setStreetName(dto.getStreetName());
        oldAddress.setCity(dto.getCity());
        oldAddress.setZipCode(dto.getZipCode());
        oldAddress.setCountry(dto.getCountry());

        Address updatedAddress = addressRepository.save(oldAddress);

        return convertToDto(updatedAddress);
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    private AddressDto convertToDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setStreetName(address.getStreetName());
        dto.setCity(address.getCity());
        dto.setZipCode(address.getZipCode());
        dto.setCountry(address.getCountry());

        return dto;
    }
}
