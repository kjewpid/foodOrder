
package se.yrgo.frontend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import se.yrgo.frontend.security.RemoteAuthenticationProvider;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RestClient restClient;

    public AdminController(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @GetMapping("/users")
    public String getAllUsers(
            Authentication authentication,
            Model model) {

        Long adminId = ((RemoteAuthenticationProvider.UserResponse)
                authentication.getPrincipal()).id();

        List<Map<String, Object>> users = restClient.get()
                .uri("http://user-service/api/users")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        model.addAttribute("users", users);
        model.addAttribute("adminId", adminId);

        return "admin-users";
    }
    @GetMapping("/users/{id}")
    public String getUser(
            Authentication authentication,
            @PathVariable Long id,
            Model model) {

        Long adminId = ((RemoteAuthenticationProvider.UserResponse)
                authentication.getPrincipal()).id();

        Map<String, Object> user = restClient.get()
                .uri("http://user-service/api/users/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        List<Map<String, Object>> addresses = restClient.get()
                .uri("http://user-service/api/users/{id}/addresses", id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        model.addAttribute("adminId", adminId);
        model.addAttribute("user", user);
        model.addAttribute("addresses", addresses);

        return "admin-user";
    }

    @PostMapping("/users/{id}/edit")
    public void editUser(
            @PathVariable Long id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            HttpServletResponse response) {

        Map<String, String> user = Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "email", email
        );

        restClient.put()
                .uri("http://user-service/api/users/{id}", id)
                .body(user)
                .retrieve()
                .toBodilessEntity();

        response.setStatus(303);
        response.setHeader("Location", "/admin/users/" + id);
    }

    @PostMapping("/users/{id}/delete")
    public void deleteUser(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletResponse response) {

        Long adminId = ((RemoteAuthenticationProvider.UserResponse)
                authentication.getPrincipal()).id();

        if (adminId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Du kan inte radera ditt eget konto"
            );
        }

        restClient.delete()
                .uri("http://user-service/api/users/{id}", id)
                .retrieve()
                .toBodilessEntity();

        response.setStatus(303);
        response.setHeader("Location", "/admin/users");
    }
}
