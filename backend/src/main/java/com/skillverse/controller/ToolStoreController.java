package com.skillverse.controller;

import com.skillverse.dto.ProductReviewRequest;
import com.skillverse.dto.StoreOrderRequest;
import com.skillverse.model.ProductReview;
import com.skillverse.model.StoreCategory;
import com.skillverse.model.StoreOrder;
import com.skillverse.model.ToolStoreProduct;
import com.skillverse.service.ToolStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store")
@CrossOrigin(origins = "*")
public class ToolStoreController {

    private final ToolStoreService storeService;

    public ToolStoreController(ToolStoreService storeService) {
        this.storeService = storeService;
    }

    // --- PRODUCTS ---

    @GetMapping("/products")
    public ResponseEntity<List<ToolStoreProduct>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(storeService.getProducts(search, category, type, serviceType, sort));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ToolStoreProduct> getProductById(@PathVariable Long id) {
        return storeService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/products")
    public ResponseEntity<ToolStoreProduct> createOrUpdateProduct(@RequestBody ToolStoreProduct product) {
        return ResponseEntity.ok(storeService.createOrUpdateProduct(product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        storeService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    // --- CATEGORIES ---

    @GetMapping("/categories")
    public ResponseEntity<List<StoreCategory>> getCategories() {
        return ResponseEntity.ok(storeService.getCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<StoreCategory> getCategoryById(@PathVariable Long id) {
        return storeService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    public ResponseEntity<StoreCategory> createOrUpdateCategory(@RequestBody StoreCategory category) {
        return ResponseEntity.ok(storeService.createOrUpdateCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        storeService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    // --- ORDERS ---

    @PostMapping("/orders/checkout")
    public ResponseEntity<?> checkout(@RequestBody StoreOrderRequest dto) {
        try {
            StoreOrder saved = storeService.checkout(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<StoreOrder>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(storeService.getOrdersByUserId(userId));
    }

    @GetMapping("/orders/booking/{bookingId}")
    public ResponseEntity<List<StoreOrder>> getOrdersByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(storeService.getOrdersByBookingId(bookingId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<StoreOrder>> getAllOrders() {
        return ResponseEntity.ok(storeService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<StoreOrder> getOrderById(@PathVariable Long id) {
        return storeService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            return storeService.updateOrderStatus(id, payload)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            return storeService.cancelOrder(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/orders/simulate-payment")
    public ResponseEntity<?> simulatePayment(@RequestBody Map<String, String> payload) {
        String orderNumber = payload.get("orderNumber");
        String transactionId = payload.get("transactionId");
        if (orderNumber == null) {
            return ResponseEntity.badRequest().body("Order number required");
        }
        return storeService.simulatePayment(orderNumber, transactionId)
                .map(order -> ResponseEntity.ok(Map.of(
                        "status", "SUCCESSFUL",
                        "orderNumber", order.getOrderNumber(),
                        "transactionId", order.getTransactionId()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- REVIEWS ---

    @GetMapping("/reviews/product/{productId}")
    public ResponseEntity<List<ProductReview>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(storeService.getProductReviews(productId));
    }

    @GetMapping("/reviews/user/{userId}")
    public ResponseEntity<List<ProductReview>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(storeService.getUserReviews(userId));
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> submitReview(@RequestBody ProductReviewRequest dto) {
        try {
            ProductReview review = storeService.submitReview(dto);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
