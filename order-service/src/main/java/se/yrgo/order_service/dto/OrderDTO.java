package se.yrgo.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import se.yrgo.order_service.enums.OrderStatus;

public record OrderDTO(Long id,
        Long userId,
        OrderStatus status,
        LocalDateTime orderDate,
        BigDecimal totalPrice,
        List<OrderItemDTO> orderItems) {
}