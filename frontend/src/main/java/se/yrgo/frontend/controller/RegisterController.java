
package se.yrgo.frontend.controller;

import jakarta.servlet.http.HttpServletResponse;
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
    public void createAccount(
            @ModelAttribute RegisterForm form,
            HttpServletResponse response) {

        try {
            restClient.post()
                    .uri("http://user-service/api/users")
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();

            response.setStatus(302);
            response.setHeader("Location", "/login?registered");

        } catch (Exception exception) {
            exception.printStackTrace();

            response.setStatus(302);
            response.setHeader("Location", "/register?error");
        }
    }
}
