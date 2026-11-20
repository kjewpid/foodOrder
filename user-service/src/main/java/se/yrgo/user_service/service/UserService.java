package se.yrgo.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.yrgo.user_service.entity.Role;
import se.yrgo.user_service.entity.User;
import se.yrgo.user_service.exception.RoleNotFoundException;
import se.yrgo.user_service.exception.UserWithIdNotFoundException;
import se.yrgo.user_service.repository.RoleRepository;
import se.yrgo.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, RoleRepository roleRepository,
                       PasswordEncoder encoder) {
        this.userRepository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                    "User with email " + user.getEmail() + " already exists!"
            );
        }

        Role userRole = roleRepository.findByRoleType(Role.RoleType.USER)
                .orElseThrow(() -> new RoleNotFoundException("User role not found"));

        user.setRole(userRole);
        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        User oldUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserWithIdNotFoundException(user.getId()));

        if (!oldUser.getFirstName().equals(user.getFirstName())) {
            oldUser.setFirstName(user.getFirstName());
        }
        if (!oldUser.getLastName().equals(user.getLastName())) {
            oldUser.setLastName(user.getLastName());
        }
        if (!oldUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists!");
            }

            oldUser.setEmail(user.getEmail());
        }

        return userRepository.save(oldUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
