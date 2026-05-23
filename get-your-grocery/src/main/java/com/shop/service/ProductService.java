package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllAvailableProducts();

    List<Product> getProductsBySeller(Long sellerId);

    Product findById(Long id);

    Product addProduct(ProductDto productDto, Long sellerId);

    Product updateProduct(Long productId, ProductDto productDto, Long sellerId);

    void deleteProduct(Long productId, Long sellerId);
}
