
package se.yrgo.frontend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.yrgo.frontend.client.UserClient;
import se.yrgo.frontend.dto.RegisterForm;
import se.yrgo.frontend.exception.UserServiceUnavailableException;

@Controller
public class RegisterController {

    private final UserClient userClient;

    public RegisterController(UserClient userClient) {
        this.userClient = userClient;
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
            userClient.createUser(form);

            response.setStatus(302);
            response.setHeader("Location", "/login?registered");

        } catch (UserServiceUnavailableException exception) {
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            response.setStatus(302);
            response.setHeader("Location", "/register?error");
        }
    }
}
