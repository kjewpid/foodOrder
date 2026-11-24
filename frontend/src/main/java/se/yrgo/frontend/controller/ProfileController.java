
package se.yrgo.frontend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.yrgo.frontend.client.UserClient;
import se.yrgo.frontend.security.RemoteAuthenticationProvider;

import java.util.List;
import java.util.Map;

@Controller
public class ProfileController {

    private final UserClient userClient;

    public ProfileController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {

        RemoteAuthenticationProvider.UserResponse principal =
                (RemoteAuthenticationProvider.UserResponse)
                        authentication.getPrincipal();

        Long userId = principal.id();
        boolean isAdmin = "ADMIN".equals(principal.role());

        model.addAttribute("user", userClient.getUser(userId));
        model.addAttribute("isAdmin", isAdmin);

        if (!isAdmin) {
            model.addAttribute("addresses",
                    userClient.getAddresses(userId));
        }

        return "profile";
    }

    @PostMapping("/profile/edit")
    public void editProfile(
            Authentication authentication,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            HttpServletResponse response) {

        Long userId = getUserId(authentication);

        Map<String, String> user = Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "email", email
        );

        userClient.updateUser(userId, user);

        response.setStatus(303);
        response.setHeader("Location", "/profile");
    }

    @PostMapping("/profile/addresses")
    public void addAddress(
            Authentication authentication,
            @RequestParam String label,
            @RequestParam String streetName,
            @RequestParam String zipCode,
            @RequestParam String city,
            @RequestParam String country,
            HttpServletResponse response) {

        Long userId = getUserId(authentication);

        Map<String, String> address = Map.of(
                "label", label,
                "streetName", streetName,
                "zipCode", zipCode,
                "city", city,
                "country", country
        );

        userClient.addAddress(userId, address);

        response.setStatus(303);
        response.setHeader("Location", "/profile");
    }

    @PostMapping("/profile/addresses/{addressId}/edit")
    public void editAddress(
            Authentication authentication,
            @PathVariable Long addressId,
            @RequestParam String label,
            @RequestParam String streetName,
            @RequestParam String zipCode,
            @RequestParam String city,
            @RequestParam String country,
            HttpServletResponse response) {

        Long userId = getUserId(authentication);

        checkAddressOwnership(userId, addressId);

        Map<String, String> address = Map.of(
                "label", label,
                "streetName", streetName,
                "zipCode", zipCode,
                "city", city,
                "country", country
        );

        userClient.updateAddress(addressId, address);

        response.setStatus(303);
        response.setHeader("Location", "/profile");
    }

    @PostMapping("/profile/addresses/{addressId}/delete")
    public void deleteAddress(
            Authentication authentication,
            @PathVariable Long addressId,
            HttpServletResponse response) {

        Long userId = getUserId(authentication);

        checkAddressOwnership(userId, addressId);

        userClient.deleteAddress(addressId);

        response.setStatus(303);
        response.setHeader("Location", "/profile");
    }

    private Long getUserId(Authentication authentication) {
        return ((RemoteAuthenticationProvider.UserResponse)
                authentication.getPrincipal()).id();
    }

    private void checkAddressOwnership(Long userId, Long addressId) {

        List<Map<String, Object>> addresses =
                userClient.getAddresses(userId);

        boolean ownsAddress = addresses != null &&
                addresses.stream().anyMatch(address ->
                        addressId.toString().equals(
                                String.valueOf(address.get("id"))
                        )
                );

        if (!ownsAddress) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
