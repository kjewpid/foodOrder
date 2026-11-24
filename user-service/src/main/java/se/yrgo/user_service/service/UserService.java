package se.yrgo.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.yrgo.user_service.entity.Role;
import se.yrgo.user_service.entity.User;
import se.yrgo.user_service.exception.RoleNotFoundException;
import se.yrgo.user_service.exception.UserWithIdNotFoundException;
import se.yrgo.user_service.messaging.MessageProducer;
import se.yrgo.user_service.repository.RoleRepository;
import se.yrgo.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final MessageProducer messageProducer;

    public UserService(UserRepository repository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       MessageProducer messageProducer) {
        this.userRepository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.messageProducer = messageProducer;
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

        User savedUser = userRepository.save(user);

        messageProducer.sendMessage("User registered: " + savedUser.getId());

        return savedUser;
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
