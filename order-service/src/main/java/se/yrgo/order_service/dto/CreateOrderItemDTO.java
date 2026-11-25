package se.yrgo.order_service.dto;

public record CreateOrderItemDTO(
        Long productId,
        Integer quantity) {
}