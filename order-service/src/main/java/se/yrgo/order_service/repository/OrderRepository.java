package se.yrgo.order_service.repository;

import se.yrgo.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByUserId(Long userId);

    Optional<Order> findByOrderId(Long id);
}
