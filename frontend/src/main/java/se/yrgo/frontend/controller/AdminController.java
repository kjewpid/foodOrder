
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

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserClient userClient;

    public AdminController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/users")
    public String getAllUsers(
            Authentication authentication,
            Model model) {

        model.addAttribute("users", userClient.getAllUsers());
        model.addAttribute("adminId", getAdminId(authentication));

        return "admin-users";
    }

    @GetMapping("/users/{id}")
    public String getUser(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        model.addAttribute("user", userClient.getUser(id));
        model.addAttribute("addresses", userClient.getAddresses(id));
        model.addAttribute("adminId", getAdminId(authentication));

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

        userClient.updateUser(id, user);

        response.setStatus(303);
        response.setHeader("Location", "/admin/users/" + id);
    }

    @PostMapping("/users/{id}/delete")
    public void deleteUser(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletResponse response) {

        Long adminId = getAdminId(authentication);

        if (adminId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Du kan inte radera ditt eget konto"
            );
        }

        userClient.deleteUser(id);

        response.setStatus(303);
        response.setHeader("Location", "/admin/users");
    }

    private Long getAdminId(Authentication authentication) {
        return ((RemoteAuthenticationProvider.UserResponse)
                authentication.getPrincipal()).id();
    }
}
