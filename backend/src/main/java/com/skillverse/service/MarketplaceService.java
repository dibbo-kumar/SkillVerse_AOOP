package com.skillverse.service;

import com.skillverse.model.MarketplaceItem;
import com.skillverse.repository.MarketplaceItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MarketplaceService {

    private final MarketplaceItemRepository marketplaceRepository;

    public MarketplaceService(MarketplaceItemRepository marketplaceRepository) {
        this.marketplaceRepository = marketplaceRepository;
    }

    public List<MarketplaceItem> getAllItems() {
        return marketplaceRepository.findAll();
    }

    public Optional<MarketplaceItem> getItemById(Long id) {
        return marketplaceRepository.findById(id);
    }

    public List<MarketplaceItem> getItemsByType(String type) {
        return marketplaceRepository.findByType(type);
    }

    public MarketplaceItem addItem(MarketplaceItem item) {
        return marketplaceRepository.save(item);
    }

    public Optional<MarketplaceItem> updateItem(Long id, MarketplaceItem updated) {
        return marketplaceRepository.findById(id).map(item -> {
            item.setTitle(updated.getTitle());
            item.setType(updated.getType());
            item.setPrice(updated.getPrice());
            item.setImageUrl(updated.getImageUrl());
            item.setDescription(updated.getDescription());
            return marketplaceRepository.save(item);
        });
    }

    public void deleteItem(Long id) {
        marketplaceRepository.deleteById(id);
    }
}
