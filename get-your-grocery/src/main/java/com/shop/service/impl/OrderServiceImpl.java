package com.shop.service.impl;

import com.shop.dto.CheckoutDto;
import com.shop.entity.Order;
import com.shop.entity.Product;
import com.shop.entity.User;
import com.shop.enums.OrderStatus;
import com.shop.exceptions.ResourceNotFoundException;
import com.shop.repository.OrderRepository;
import com.shop.repository.ProductRepository;
import com.shop.repository.UserRepository;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public Order placeOrder(CheckoutDto checkoutDto, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Product product = productRepository.findById(checkoutDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < checkoutDto.getQuantity()) {
            throw new IllegalArgumentException(
                "Not enough stock. Available: " + product.getStockQuantity());
        }

        double totalPrice = product.getPrice() * checkoutDto.getQuantity();

        Order order = Order.builder()
                .customer(customer)
                .seller(product.getSeller())
                .product(product)
                .customerUsername(customer.getUsername())
                .deliveryAddress(checkoutDto.getDeliveryAddress())
                .contactNumber(checkoutDto.getContactNumber())
                .quantity(checkoutDto.getQuantity())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .build();

        // Reduce stock
        product.setStockQuantity(product.getStockQuantity() - checkoutDto.getQuantity());
        productRepository.save(product);

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return orderRepository.findByCustomer(customer);
    }

    @Override
    public List<Order> getOrdersBySeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return orderRepository.findBySeller(seller);
    }

    @Override
    public Order deliverOrder(Long orderId, Long sellerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getSeller().getId().equals(sellerId)) {
            throw new ResourceNotFoundException("You are not authorized to update this order");
        }

        order.setStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    @Override
    public double getTodayEarnings(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        Double total = orderRepository.findTotalEarningsBySeller(
                seller, OrderStatus.DELIVERED, startOfDay, endOfDay);

        return total != null ? total : 0.0;
    }
}
