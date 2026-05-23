package com.shop.service.impl;

import com.shop.entity.Product;
import com.shop.entity.SavedItem;
import com.shop.entity.User;
import com.shop.exceptions.ResourceNotFoundException;
import com.shop.repository.ProductRepository;
import com.shop.repository.SavedItemRepository;
import com.shop.repository.UserRepository;
import com.shop.service.SavedItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedItemServiceImpl implements SavedItemService {

    private final SavedItemRepository savedItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void saveItem(Long customerId, Long productId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Don't add duplicate
        if (!savedItemRepository.existsByCustomerAndProduct(customer, product)) {
            SavedItem savedItem = SavedItem.builder()
                    .customer(customer)
                    .product(product)
                    .build();
            savedItemRepository.save(savedItem);
        }
    }

    @Override
    public void removeSavedItem(Long savedItemId, Long customerId) {
        SavedItem savedItem = savedItemRepository.findById(savedItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved item not found"));

        if (!savedItem.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("You are not authorized to remove this saved item");
        }

        savedItemRepository.delete(savedItem);
    }

    @Override
    public List<SavedItem> getSavedItems(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return savedItemRepository.findByCustomer(customer);
    }
}
