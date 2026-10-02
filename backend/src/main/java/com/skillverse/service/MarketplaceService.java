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

    public List<MarketplaceItem> getAll() {
        return marketplaceRepository.findAll();
    }

    public Optional<MarketplaceItem> getById(Long id) {
        return marketplaceRepository.findById(id);
    }

    public List<MarketplaceItem> getItemsByType(String type) {
        return marketplaceRepository.findByType(type);
    }

    public MarketplaceItem save(MarketplaceItem item) {
        return marketplaceRepository.save(item);
    }

    public Optional<MarketplaceItem> update(Long id, MarketplaceItem item) {
        return marketplaceRepository.findById(id).map(existing -> {
            existing.setTitle(item.getTitle());
            existing.setDescription(item.getDescription());
            existing.setPrice(item.getPrice());
            existing.setType(item.getType());
            existing.setImageUrl(item.getImageUrl());
            return marketplaceRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (marketplaceRepository.existsById(id)) {
            marketplaceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
