package se.yrgo.frontend.controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
                .body(new ParameterizedTypeReference<>() {});

        model.addAttribute("addresses", addresses);

        model.addAttribute("user", user);

        return "profile";
    }
}