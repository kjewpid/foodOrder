package se.yrgo.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.yrgo.user_service.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(Role.RoleType roleType);
}
