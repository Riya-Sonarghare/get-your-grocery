package com.shop.service;

import com.shop.entity.SavedItem;

import java.util.List;

public interface SavedItemService {

    void saveItem(Long customerId, Long productId);

    void removeSavedItem(Long savedItemId, Long customerId);

    List<SavedItem> getSavedItems(Long customerId);
}
