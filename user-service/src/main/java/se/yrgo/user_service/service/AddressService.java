
package se.yrgo.user_service.service;

import org.springframework.stereotype.Service;
import se.yrgo.user_service.dto.AddressDto;
import se.yrgo.user_service.dto.CreateAddressDto;
import se.yrgo.user_service.entity.Address;
import se.yrgo.user_service.entity.User;
import se.yrgo.user_service.exception.UserWithIdNotFoundException;
import se.yrgo.user_service.messaging.MessageProducer;
import se.yrgo.user_service.repository.AddressRepository;
import se.yrgo.user_service.repository.UserRepository;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final MessageProducer messageProducer;

    public AddressService(AddressRepository addressRepository,
                          UserRepository userRepository,
                          MessageProducer messageProducer) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.messageProducer = messageProducer;
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
        address.setLabel(dto.label());
        address.setStreetName(dto.streetName());
        address.setCity(dto.city());
        address.setZipCode(dto.zipCode());
        address.setCountry(dto.country());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        messageProducer.sendMessage(
                "address.created",
                savedAddress.getId().toString()
        );

        return convertToDto(savedAddress);
    }

    public AddressDto updateAddress(Long addressId, CreateAddressDto dto) {
        Address oldAddress = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Address not found: " + addressId));

        oldAddress.setLabel(dto.label());
        oldAddress.setStreetName(dto.streetName());
        oldAddress.setCity(dto.city());
        oldAddress.setZipCode(dto.zipCode());
        oldAddress.setCountry(dto.country());

        Address updatedAddress = addressRepository.save(oldAddress);

        messageProducer.sendMessage(
                "address.updated",
                updatedAddress.getId().toString()
        );
        return convertToDto(updatedAddress);
    }

    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("Address not found: " + id);
        }
        
        addressRepository.deleteById(id);

        messageProducer.sendMessage(
                "address.deleted",
                id.toString()
        );
    }

    private AddressDto convertToDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getLabel(),
                address.getStreetName(),
                address.getCity(),
                address.getZipCode(),
                address.getCountry()
        );
    }
}
