package com.skillverse.controller;

import com.skillverse.model.MarketplaceItem;
import com.skillverse.service.MarketplaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping
    public ResponseEntity<List<MarketplaceItem>> getItems() {
        return ResponseEntity.ok(marketplaceService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketplaceItem> getItemById(@PathVariable Long id) {
        return marketplaceService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MarketplaceItem>> getItemsByType(@RequestParam String type) {
        return ResponseEntity.ok(marketplaceService.getItemsByType(type));
    }

    @PostMapping
    public ResponseEntity<MarketplaceItem> addItem(@RequestBody MarketplaceItem item) {
        return ResponseEntity.ok(marketplaceService.addItem(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarketplaceItem> updateItem(@PathVariable Long id, @RequestBody MarketplaceItem item) {
        return marketplaceService.updateItem(id, item)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        marketplaceService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}
