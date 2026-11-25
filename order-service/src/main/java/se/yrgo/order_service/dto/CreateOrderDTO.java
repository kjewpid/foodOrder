package se.yrgo.order_service.dto;

import java.util.List;

public record CreateOrderDTO(
        Long userId,
        List<CreateOrderItemDTO> orderItems) {
}
