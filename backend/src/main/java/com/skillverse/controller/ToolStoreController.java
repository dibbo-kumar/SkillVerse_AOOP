package com.skillverse.controller;

import com.skillverse.dto.OrderRequestDTO;
import com.skillverse.dto.ReviewDTO;
import com.skillverse.model.*;
import com.skillverse.service.ToolStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Tool Store Products, Orders, Categories, Reviews, and Analytics.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/store")
@CrossOrigin(origins = "*")
public class ToolStoreController {

    private final ToolStoreService storeService;

    public ToolStoreController(ToolStoreService storeService) {
        this.storeService = storeService;
    }

    // ==========================================
    // PRODUCTS ENDPOINTS
    // ==========================================

    /**
     * Search and filter products with RequestParam
     */
    @GetMapping("/products")
    public ResponseEntity<List<ToolStoreProduct>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(storeService.getProducts(search, category, type, serviceType, sort));
    }

    /**
     * Standard CRUD: Get Product by ID with PathVariable
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ToolStoreProduct> getById(@PathVariable Long id) {
        return storeService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Save Product with RequestBody
     */
    @PostMapping("/products")
    public ResponseEntity<ToolStoreProduct> save(@RequestBody ToolStoreProduct product) {
        return ResponseEntity.ok(storeService.saveProduct(product));
    }

    /**
     * Standard CRUD: Update Product with PathVariable and RequestBody
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ToolStoreProduct product) {
        return storeService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Delete Product with PathVariable
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (storeService.deleteProduct(id)) {
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // CATEGORIES ENDPOINTS
    // ==========================================

    @GetMapping("/categories")
    public ResponseEntity<List<StoreCategory>> getCategories() {
        return ResponseEntity.ok(storeService.getCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<StoreCategory> saveCategory(@RequestBody StoreCategory category) {
        return ResponseEntity.ok(storeService.saveCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (storeService.deleteCategory(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // ORDERS ENDPOINTS
    // ==========================================

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO dto) {
        try {
            return ResponseEntity.ok(storeService.createOrder(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<StoreOrder>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(storeService.getUserOrders(userId));
    }

    @GetMapping("/orders/number/{orderNumber}")
    public ResponseEntity<StoreOrder> getOrderByNumber(@PathVariable String orderNumber) {
        return storeService.getOrderByNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orders/booking/{bookingId}")
    public ResponseEntity<List<StoreOrder>> getOrdersByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(storeService.getOrdersByBooking(bookingId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<StoreOrder>> getAllOrders() {
        return ResponseEntity.ok(storeService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String newStatus = payload.get("orderStatus");
            String paymentStatus = payload.get("paymentStatus");
            return ResponseEntity.ok(storeService.updateOrderStatus(id, newStatus, paymentStatus));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(storeService.cancelOrder(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/payment/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {
        String orderNumber = payload.get("orderNumber");
        String transactionId = payload.get("transactionId");
        if (orderNumber == null) {
            return ResponseEntity.badRequest().body("Order number required");
        }
        try {
            StoreOrder order = storeService.verifyPayment(orderNumber, transactionId);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESSFUL",
                    "orderNumber", order.getOrderNumber(),
                    "transactionId", order.getTransactionId()
            ));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // REVIEWS ENDPOINTS
    // ==========================================

    @GetMapping("/reviews/product/{productId}")
    public ResponseEntity<List<ProductReview>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(storeService.getProductReviews(productId));
    }

    @GetMapping("/reviews/user/{userId}")
    public ResponseEntity<List<ProductReview>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(storeService.getUserReviews(userId));
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> submitReview(@RequestBody ReviewDTO dto) {
        try {
            return ResponseEntity.ok(storeService.submitReview(dto));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid product or user ID");
        }
    }

    // ==========================================
    // ANALYTICS ENDPOINT
    // ==========================================

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(storeService.getAnalytics());
    }
}
