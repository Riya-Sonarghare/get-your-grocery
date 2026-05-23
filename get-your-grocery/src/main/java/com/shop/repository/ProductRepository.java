package com.shop.repository;

import com.shop.entity.Product;
import com.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySeller(User seller);

    List<Product> findByAvailableTrue();
}
