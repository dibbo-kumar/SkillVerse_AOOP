package com.skillverse.controller;

import com.skillverse.model.MarketplaceItem;
import com.skillverse.service.MarketplaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Marketplace Items.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    /**
     * Standard CRUD: Get all items
     */
    @GetMapping
    public ResponseEntity<List<MarketplaceItem>> getAll() {
        return ResponseEntity.ok(marketplaceService.getAll());
    }

    /**
     * Standard CRUD: Get Item by ID with PathVariable
     */
    @GetMapping("/{id}")
    public ResponseEntity<MarketplaceItem> getById(@PathVariable Long id) {
        return marketplaceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Filter items by type with RequestParam
     */
    @GetMapping("/filter")
    public ResponseEntity<List<MarketplaceItem>> getItemsByType(@RequestParam String type) {
        return ResponseEntity.ok(marketplaceService.getItemsByType(type));
    }

    /**
     * Standard CRUD: Save Item with RequestBody
     */
    @PostMapping
    public ResponseEntity<MarketplaceItem> save(@RequestBody MarketplaceItem item) {
        return ResponseEntity.ok(marketplaceService.save(item));
    }

    /**
     * Standard CRUD: Update Item with PathVariable and RequestBody
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MarketplaceItem item) {
        return marketplaceService.update(id, item)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Delete Item with PathVariable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (marketplaceService.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Marketplace item deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}
