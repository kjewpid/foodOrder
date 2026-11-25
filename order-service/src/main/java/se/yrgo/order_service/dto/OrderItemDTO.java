package se.yrgo.order_service.dto;

import java.math.BigDecimal;

public record OrderItemDTO(Long id,
        Long productId,
        Integer quantity,
        BigDecimal price) {
}