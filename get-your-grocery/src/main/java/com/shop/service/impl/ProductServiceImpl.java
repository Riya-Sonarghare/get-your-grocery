package com.shop.service.impl;

import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.User;
import com.shop.exceptions.ResourceNotFoundException;
import com.shop.repository.ProductRepository;
import com.shop.repository.UserRepository;
import com.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<Product> getAllAvailableProducts() {
        return productRepository.findByAvailableTrue();
    }

    @Override
    public List<Product> getProductsBySeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return productRepository.findBySeller(seller);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product addProduct(ProductDto productDto, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stockQuantity(productDto.getStockQuantity())
                .category(productDto.getCategory())
                .available(true)
                .seller(seller)
                .build();

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, ProductDto productDto, Long sellerId) {
        Product product = findById(productId);

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new ResourceNotFoundException("You are not authorized to edit this product");
        }

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(productDto.getCategory());
        product.setAvailable(productDto.isAvailable());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = findById(productId);

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new ResourceNotFoundException("You are not authorized to delete this product");
        }

        productRepository.delete(product);
    }
}
