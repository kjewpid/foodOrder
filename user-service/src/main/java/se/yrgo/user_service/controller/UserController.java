
package se.yrgo.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import se.yrgo.user_service.dto.AuthResponseDto;
import se.yrgo.user_service.dto.CreateUserDto;
import se.yrgo.user_service.dto.LoginDto;
import se.yrgo.user_service.dto.UserDto;
import se.yrgo.user_service.entity.User;
import se.yrgo.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService service, PasswordEncoder encoder) {
        this.service = service;
        this.passwordEncoder = encoder;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {

        List<UserDto> users = service.getAllUsers().stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {

        return service.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody CreateUserDto createUserDto) {

        User user = new User();

        user.setFirstName(createUserDto.firstName());
        user.setLastName(createUserDto.lastName());
        user.setEmail(createUserDto.email());
        user.setPassword(createUserDto.password());

        User createdUser = service.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDto(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDto) {

        User user = new User();

        user.setId(id);
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());

        User updatedUser = service.updateUser(user);

        return ResponseEntity.ok(toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        service.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody LoginDto login) {

        User user = service.findByEmail(login.email())
                .orElse(null);

        if (user == null ||
                !passwordEncoder.matches(
                        login.password(), user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(
                new AuthResponseDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole().getRoleType().name()
                )
        );
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getRoleType().name()
        );
    }
}
