package se.yrgo.frontend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import se.yrgo.frontend.security.RemoteAuthenticationProvider;

import java.util.List;
import java.util.Map;

@Controller
public class ProfileController {

    private final RestClient restClient;

    public ProfileController(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        Long userId = ((RemoteAuthenticationProvider.UserResponse)
                authentication.getPrincipal()).id();

        Map user = restClient.get()
                .uri("http://user-service/api/users/{id}", userId)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> addresses = restClient.get()
                .uri("http://user-service/api/users/{id}/addresses", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        model.addAttribute("addresses", addresses);

        model.addAttribute("user", user);

        return "profile";
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

        Long userId = ((RemoteAuthenticationProvider.UserResponse)
                authentication.getPrincipal()).id();

        Map<String, String> address = Map.of(
                "label", label,
                "streetName", streetName,
                "zipCode", zipCode,
                "city", city,
                "country", country
        );

        restClient.post()
                .uri("http://user-service/api/users/{id}/addresses", userId)
                .body(address)
                .retrieve()
                .toBodilessEntity();

        response.setStatus(303);
        response.setHeader("Location", "/profile");
    }

    @PostMapping("/profile/addresses/{addressId}/edit")
    public void editAddress(
            @PathVariable Long addressId,
            @RequestParam String label,
            @RequestParam String streetName,
            @RequestParam String zipCode,
            @RequestParam String city,
            @RequestParam String country,
            HttpServletResponse response) {

        Map<String, String> address = Map.of(
                "label", label,
                "streetName", streetName,
                "zipCode", zipCode,
                "city", city,
                "country", country
        );

        restClient.put()
                .uri("http://user-service/api/addresses/{id}", addressId)
                .body(address)
                .retrieve()
                .toBodilessEntity();

        response.setStatus(303);
        response.setHeader("Location", "/profile");
    }
}