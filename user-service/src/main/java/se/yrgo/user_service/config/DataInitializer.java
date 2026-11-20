package se.yrgo.user_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import se.yrgo.user_service.entity.Role;
import se.yrgo.user_service.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args){
        if (roleRepository.findByRoleType(Role.RoleType.USER).isEmpty()){
            roleRepository.save(new Role(Role.RoleType.USER));
        }

        if (roleRepository.findByRoleType(Role.RoleType.ADMIN).isEmpty()){
            roleRepository.save(new Role(Role.RoleType.ADMIN));
        }
    }
}
