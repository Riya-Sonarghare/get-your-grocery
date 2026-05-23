package com.shop.repository;

import com.shop.entity.Product;
import com.shop.entity.SavedItem;
import com.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedItemRepository extends JpaRepository<SavedItem, Long> {

    List<SavedItem> findByCustomer(User customer);

    boolean existsByCustomerAndProduct(User customer, Product product);
}
