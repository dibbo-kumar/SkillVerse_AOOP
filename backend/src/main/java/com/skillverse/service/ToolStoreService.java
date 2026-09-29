package com.skillverse.service;

import com.skillverse.dto.ProductReviewRequest;
import com.skillverse.dto.StoreOrderRequest;
import com.skillverse.model.*;
import com.skillverse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ToolStoreService {

    private final ToolStoreProductRepository productRepository;
    private final StoreCategoryRepository categoryRepository;
    private final StoreOrderRepository orderRepository;
    private final ProductReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ServiceBookingRepository bookingRepository;

    public ToolStoreService(ToolStoreProductRepository productRepository,
                            StoreCategoryRepository categoryRepository,
                            StoreOrderRepository orderRepository,
                            ProductReviewRepository reviewRepository,
                            UserRepository userRepository,
                            ServiceBookingRepository bookingRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    // --- PRODUCTS ---

    public List<ToolStoreProduct> getProducts(String search, String category, String type, String serviceType, String sort) {
        List<ToolStoreProduct> list;
        if (search != null && !search.trim().isEmpty()) {
            list = productRepository.searchProducts(search.trim());
        } else {
            list = productRepository.findAll();
        }

        if (serviceType != null && !serviceType.trim().isEmpty() && !serviceType.equalsIgnoreCase("All")) {
            String s = serviceType.trim().toLowerCase();
            list = list.stream().filter(p -> p.getCompatibleServices() != null &&
                    (p.getCompatibleServices().toLowerCase().contains(s) || "general".equalsIgnoreCase(p.getCompatibleServices()))
            ).collect(Collectors.toList());
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            list = list.stream().filter(p -> p.getCategory() != null &&
                    p.getCategory().equalsIgnoreCase(category.trim())
            ).collect(Collectors.toList());
        }

        if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("All")) {
            list = list.stream().filter(p -> p.getType() != null &&
                    p.getType().equalsIgnoreCase(type.trim())
            ).collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "price_asc":
                    list.sort(Comparator.comparing(p -> p.getPrice() != null ? p.getPrice() : 0.0));
                    break;
                case "price_desc":
                    list.sort((a, b) -> Double.compare(b.getPrice() != null ? b.getPrice() : 0.0, a.getPrice() != null ? a.getPrice() : 0.0));
                    break;
                case "rating":
                    list.sort((a, b) -> Double.compare(b.getRating() != null ? b.getRating() : 0.0, a.getRating() != null ? a.getRating() : 0.0));
                    break;
                case "popular":
                    list.sort((a, b) -> Integer.compare(b.getReviewCount() != null ? b.getReviewCount() : 0, a.getReviewCount() != null ? a.getReviewCount() : 0));
                    break;
            }
        }
        return list;
    }

    public Optional<ToolStoreProduct> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public ToolStoreProduct createOrUpdateProduct(ToolStoreProduct product) {
        if (product.getOldPrice() != null && product.getPrice() != null && product.getOldPrice() > product.getPrice()) {
            double discount = ((product.getOldPrice() - product.getPrice()) / product.getOldPrice()) * 100.0;
            product.setDiscountPercent((int) Math.round(discount));
        } else {
            product.setDiscountPercent(0);
        }
        if (product.getStockQuantity() != null) {
            product.setAvailable(product.getStockQuantity() > 0);
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // --- CATEGORIES ---

    public List<StoreCategory> getCategories() {
        return categoryRepository.findAll();
    }

    public Optional<StoreCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public StoreCategory createOrUpdateCategory(StoreCategory category) {
        if (category.getSlug() == null || category.getSlug().isEmpty()) {
            category.setSlug(category.getName().toLowerCase().replaceAll("[^a-z0-9]", "-"));
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // --- ORDERS ---

    public StoreOrder checkout(StoreOrderRequest dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }

        User user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId()).orElse(null);
        }

        ServiceBooking booking = null;
        if (dto.getServiceBookingId() != null) {
            booking = bookingRepository.findById(dto.getServiceBookingId()).orElse(null);
        }

        String orderNumber = "ORD-" + System.currentTimeMillis();
        StoreOrder order = new StoreOrder();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setServiceBooking(booking);
        order.setCustomerName(dto.getCustomerName());
        order.setPhone(dto.getPhone());
        order.setAddress(dto.getAddress());
        order.setDivision(dto.getDivision());
        order.setDistrict(dto.getDistrict());
        order.setArea(dto.getArea());
        order.setPostalCode(dto.getPostalCode());
        order.setDeliveryInstructions(dto.getDeliveryInstructions());
        order.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "CASH_ON_DELIVERY");

        double subtotal = 0.0;
        List<StoreOrderItem> orderItems = new ArrayList<>();

        for (StoreOrderRequest.CartItemDTO itemDto : dto.getItems()) {
            ToolStoreProduct product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found ID: " + itemDto.getProductId()));

            int qty = (itemDto.getQuantity() != null && itemDto.getQuantity() > 0) ? itemDto.getQuantity() : 1;
            double unitPrice = product.getPrice() != null ? product.getPrice() : 0.0;
            double totalPrice = unitPrice * qty;

            StoreOrderItem item = new StoreOrderItem(product, qty, unitPrice);
            item.setOrder(order);
            orderItems.add(item);
            subtotal += totalPrice;

            if (product.getStockQuantity() != null) {
                int newStock = Math.max(0, product.getStockQuantity() - qty);
                product.setStockQuantity(newStock);
                product.setAvailable(newStock > 0);
                productRepository.save(product);
            }
        }

        double deliveryFee = subtotal > 3000 ? 0.0 : 60.0;
        double totalAmount = subtotal + deliveryFee;

        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus("ORDER_PLACED");
        order.setPaymentStatus("CASH_ON_DELIVERY".equalsIgnoreCase(order.getPaymentMethod()) ? "PENDING" : "PENDING");
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    public List<StoreOrder> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByPlacedAtDesc(userId);
    }

    public List<StoreOrder> getOrdersByBookingId(Long bookingId) {
        return orderRepository.findByServiceBookingId(bookingId);
    }

    public List<StoreOrder> getAllOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc();
    }

    public Optional<StoreOrder> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    private int getStatusRank(String status) {
        if (status == null) return 0;
        switch (status.toUpperCase()) {
            case "ORDER_PLACED": return 1;
            case "PAYMENT_CONFIRMED": return 2;
            case "PROCESSING": return 3;
            case "PACKED": return 4;
            case "SHIPPED": return 5;
            case "OUT_FOR_DELIVERY": return 6;
            case "DELIVERED": return 7;
            case "CANCELLED": return 99;
            default: return 0;
        }
    }

    public Optional<StoreOrder> updateOrderStatus(Long id, Map<String, String> payload) {
        return orderRepository.findById(id).map(order -> {
            if (payload.containsKey("orderStatus")) {
                String newStatus = payload.get("orderStatus");
                int currentRank = getStatusRank(order.getOrderStatus());
                int newRank = getStatusRank(newStatus);

                if (newRank < currentRank && newRank != 99) {
                    throw new IllegalArgumentException("Order status cannot be moved backwards from " + order.getOrderStatus() + " to " + newStatus);
                }
                order.setOrderStatus(newStatus);
                if ("DELIVERED".equalsIgnoreCase(newStatus)) {
                    order.setPaymentStatus("SUCCESSFUL");
                }
            }
            if (payload.containsKey("paymentStatus")) {
                order.setPaymentStatus(payload.get("paymentStatus"));
            }
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        });
    }

    public Optional<StoreOrder> cancelOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            if ("DELIVERED".equalsIgnoreCase(order.getOrderStatus())) {
                throw new IllegalStateException("Delivered orders cannot be cancelled");
            }
            order.setOrderStatus("CANCELLED");
            order.setUpdatedAt(LocalDateTime.now());

            for (StoreOrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getProduct().getStockQuantity() != null) {
                    ToolStoreProduct prod = item.getProduct();
                    prod.setStockQuantity(prod.getStockQuantity() + item.getQuantity());
                    prod.setAvailable(true);
                    productRepository.save(prod);
                }
            }
            return orderRepository.save(order);
        });
    }

    public Optional<StoreOrder> simulatePayment(String orderNumber, String transactionId) {
        return orderRepository.findByOrderNumber(orderNumber).map(order -> {
            order.setPaymentStatus("SUCCESSFUL");
            order.setOrderStatus("PAYMENT_CONFIRMED");
            order.setTransactionId(transactionId != null ? transactionId : "MOCK-TXN-" + System.currentTimeMillis());
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        });
    }

    // --- REVIEWS ---

    public List<ProductReview> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public List<ProductReview> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public ProductReview submitReview(ProductReviewRequest dto) {
        ToolStoreProduct product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        boolean isDeliveredPurchase = orderRepository.findByUserIdOrderByPlacedAtDesc(dto.getUserId()).stream()
                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getOrderStatus()))
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(dto.getProductId()));

        if (!isDeliveredPurchase) {
            throw new IllegalStateException("You can only submit a review after the product has been delivered to you.");
        }

        if (reviewRepository.existsByProductIdAndUserId(dto.getProductId(), dto.getUserId())) {
            throw new IllegalStateException("You have already reviewed this product.");
        }

        ProductReview review = new ProductReview(product, user, dto.getRating() != null ? dto.getRating() : 5,
                dto.getComment(), true);
        if (dto.getPhotoUrl() != null) {
            review.setPhotoUrl(dto.getPhotoUrl());
        }
        ProductReview saved = reviewRepository.save(review);

        List<ProductReview> allReviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(dto.getProductId());
        double avg = allReviews.stream().mapToInt(ProductReview::getRating).average().orElse(5.0);
        product.setRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(allReviews.size());
        productRepository.save(product);

        return saved;
    }
}
