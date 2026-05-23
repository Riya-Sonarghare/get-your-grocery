package com.shop.service;

import com.shop.dto.CheckoutDto;
import com.shop.entity.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(CheckoutDto checkoutDto, Long customerId);

    List<Order> getOrdersByCustomer(Long customerId);

    List<Order> getOrdersBySeller(Long sellerId);

    Order deliverOrder(Long orderId, Long sellerId);

    double getTodayEarnings(Long sellerId);
}
