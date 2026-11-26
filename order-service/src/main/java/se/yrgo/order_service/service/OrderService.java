package se.yrgo.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.yrgo.order_service.dto.CreateOrderDTO;
import se.yrgo.order_service.dto.CreateOrderItemDTO;
import se.yrgo.order_service.dto.OrderDTO;
import se.yrgo.order_service.dto.OrderItemDTO;
import se.yrgo.order_service.entity.Order;
import se.yrgo.order_service.entity.OrderItem;
import se.yrgo.order_service.enums.OrderStatus;
import se.yrgo.order_service.exception.OrderWithIdNotFoundException;
import se.yrgo.order_service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderDTO dto) {

        if (dto.orderItems() == null || dto.orderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order(
                dto.userId(),
                OrderStatus.PENDING,
                LocalDateTime.now(),
                BigDecimal.ZERO);// Should be changed with Product Service

        BigDecimal totalPrice = BigDecimal.ZERO;// Should be changed with Product Service

        for (CreateOrderItemDTO itemDTO : dto.orderItems()) {

            if (itemDTO.quantity() == null || itemDTO.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }

            if (itemDTO.productId() == null) {
                throw new IllegalArgumentException("productId can not be null");
            }

            OrderItem item = new OrderItem(
                    itemDTO.productId(),
                    itemDTO.quantity(),
                    BigDecimal.ZERO); // Should be changed with Product Service

            order.addOrderItem(item);
        }

        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        return convertToDto(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderWithIdNotFoundException(id));

        if (!isValidStatusChange(order.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid status change");
        }

        order.setStatus(newStatus);

        return convertToDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderWithIdNotFoundException(id));

        return convertToDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // Convert to DTO:s

    private OrderDTO convertToDto(Order order) {
        List<OrderItemDTO> items = order.getOrderItems()
                .stream()
                .map(this::convertOrderItemToDto)
                .toList();

        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getOrderDate(),
                order.getTotalPrice(),
                items);
    }

    private OrderItemDTO convertOrderItemToDto(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getPrice());
    }

    // Help methods

    private boolean isValidStatusChange(OrderStatus current, OrderStatus next) {

        return switch (current) {
            case PENDING ->
                next == OrderStatus.CONFIRMED ||
                        next == OrderStatus.CANCELLED;

            case CONFIRMED ->
                next == OrderStatus.SHIPPED ||
                        next == OrderStatus.CANCELLED;

            case SHIPPED ->
                next == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };
    }
}
