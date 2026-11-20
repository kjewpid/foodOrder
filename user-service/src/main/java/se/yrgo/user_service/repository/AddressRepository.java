package se.yrgo.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.yrgo.user_service.entity.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);
}
