package se.yrgo.user_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.yrgo.user_service.entity.Role;
import se.yrgo.user_service.entity.User;
import se.yrgo.user_service.repository.RoleRepository;
import se.yrgo.user_service.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args){
        if (roleRepository.findByRoleType(Role.RoleType.USER).isEmpty()){
            roleRepository.save(new Role(Role.RoleType.USER));
        }

        if (roleRepository.findByRoleType(Role.RoleType.ADMIN).isEmpty()){
            roleRepository.save(new Role(Role.RoleType.ADMIN));
        }

        if (userRepository.findByEmail("admin@foodorder.com").isEmpty()){

            Role adminRole = roleRepository.findByRoleType(Role.RoleType.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = new User();
            admin.setFirstName("FoodOrder");
            admin.setLastName("Admin");
            admin.setEmail("admin@foodorder.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setRole(adminRole);

            userRepository.save(admin);
        }
    }
}
