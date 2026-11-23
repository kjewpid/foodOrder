package se.yrgo.frontend.controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import se.yrgo.frontend.dto.RegisterForm;

@Controller
public class RegisterController {

    private final RestClient restClient;

    public RegisterController(
            @LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(@ModelAttribute RegisterForm form) {
        try {
            restClient.post()
                    .uri("http://user-service/api/users")
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/login?registered";

        } catch (Exception exception) {
            exception.printStackTrace();
            return "redirect:/register?error";
        }
    }
}