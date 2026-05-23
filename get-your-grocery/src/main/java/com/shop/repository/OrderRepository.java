package com.shop.repository;

import com.shop.entity.Order;
import com.shop.entity.User;
import com.shop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(User customer);

    List<Order> findBySeller(User seller);

    // Sum up delivered orders for a seller within a date range (for earnings)
    @Query("SELECT SUM(o.totalPrice) FROM Order o " +
           "WHERE o.seller = :seller " +
           "AND o.status = :status " +
           "AND o.orderDate >= :start " +
           "AND o.orderDate <= :end")
    Double findTotalEarningsBySeller(@Param("seller") User seller,
                                     @Param("status") OrderStatus status,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}
