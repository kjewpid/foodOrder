
package se.yrgo.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.yrgo.user_service.dto.AddressDto;
import se.yrgo.user_service.dto.CreateAddressDto;
import se.yrgo.user_service.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/users/{userId}/addresses")
    public ResponseEntity<List<AddressDto>> getAddressesByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                addressService.getAddressesByUserId(userId));
    }

    @PostMapping("/users/{userId}/addresses")
    public ResponseEntity<AddressDto> createAddress(
            @PathVariable Long userId,
            @RequestBody CreateAddressDto addressDto) {

        AddressDto createdAddress =
                addressService.createAddress(userId, addressDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdAddress);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long addressId,
            @RequestBody CreateAddressDto addressDto) {

        AddressDto updatedAddress =
                addressService.updateAddress(addressId, addressDto);

        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId) {

        addressService.deleteAddress(addressId);

        return ResponseEntity.noContent().build();
    }
}
