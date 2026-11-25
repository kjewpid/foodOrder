package se.yrgo.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(Long id,
        Long userId,
        String status,
        LocalDateTime orderDate,
        BigDecimal totalPrice,
        List<OrderItemDTO> orderItems) {
}