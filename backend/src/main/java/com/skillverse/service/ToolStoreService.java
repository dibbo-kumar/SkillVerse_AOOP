package com.skillverse.service;

import com.skillverse.dto.CartItemDTO;
import com.skillverse.dto.OrderRequestDTO;
import com.skillverse.dto.ReviewDTO;
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

    public List<ToolStoreProduct> getProducts(String search, String category, String type, String serviceType, String sort) {
        List<ToolStoreProduct> list;
        if (search != null && !search.trim().isEmpty()) {
            list = productRepository.searchProducts(search.trim());
        } else {
            list = productRepository.findAll();
        }

        if (serviceType != null && !serviceType.trim().isEmpty() && !serviceType.equalsIgnoreCase("All")) {
            String s = serviceType.trim().toLowerCase();
            list = list.stream()
                    .filter(p -> p.getCompatibleServices() != null && p.getCompatibleServices().toLowerCase().contains(s))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            String c = category.trim().toLowerCase();
            list = list.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().toLowerCase().contains(c))
                    .collect(Collectors.toList());
        }

        if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("All")) {
            String t = type.trim().toLowerCase();
            list = list.stream()
                    .filter(p -> p.getType() != null && p.getType().toLowerCase().contains(t))
                    .collect(Collectors.toList());
        }

        if ("price_asc".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(ToolStoreProduct::getPrice));
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(ToolStoreProduct::getPrice).reversed());
        } else if ("rating".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(ToolStoreProduct::getRating).reversed());
        }

        return list;
    }

    public Optional<ToolStoreProduct> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public ToolStoreProduct saveProduct(ToolStoreProduct product) {
        if (product.getStockQuantity() == null) product.setStockQuantity(10);
        if (product.getLowStockThreshold() == null) product.setLowStockThreshold(3);
        if (product.getRating() == null) product.setRating(5.0);
        if (product.getReviewCount() == null) product.setReviewCount(0);
        if (product.getType() == null) product.setType("SPARE_PART");
        return productRepository.save(product);
    }

    public Optional<ToolStoreProduct> updateProduct(Long id, ToolStoreProduct details) {
        return productRepository.findById(id).map(p -> {
            p.setTitle(details.getTitle());
            p.setDescription(details.getDescription());
            p.setPrice(details.getPrice());
            p.setCategory(details.getCategory());
            p.setType(details.getType());
            p.setImageUrl(details.getImageUrl());
            p.setAvailable(details.isAvailable());
            p.setStockQuantity(details.getStockQuantity());
            p.setLowStockThreshold(details.getLowStockThreshold());
            p.setCompatibleServices(details.getCompatibleServices());
            p.setWarranty(details.getWarranty());
            p.setSpecifications(details.getSpecifications());
            p.setBrand(details.getBrand());
            return productRepository.save(p);
        });
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<StoreCategory> getCategories() {
        return categoryRepository.findAll();
    }

    public StoreCategory saveCategory(StoreCategory category) {
        return categoryRepository.save(category);
    }

    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public StoreOrder createOrder(OrderRequestDTO dto) {
        if (dto.userId == null || dto.items == null || dto.items.isEmpty()) {
            throw new IllegalArgumentException("User ID and non-empty cart items required");
        }

        User user = userRepository.findById(dto.userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + dto.userId));

        ServiceBooking booking = null;
        if (dto.serviceBookingId != null) {
            booking = bookingRepository.findById(dto.serviceBookingId).orElse(null);
        }

        StoreOrder order = new StoreOrder();
        order.setUser(user);
        order.setServiceBooking(booking);
        order.setCustomerName(dto.customerName != null ? dto.customerName : user.getName());
        order.setPhone(dto.phone != null ? dto.phone : user.getPhone());
        order.setAddress(dto.address != null ? dto.address : user.getAddress());
        order.setDivision(dto.division != null ? dto.division : "Dhaka");
        order.setDistrict(dto.district != null ? dto.district : "Dhaka");
        order.setArea(dto.area != null ? dto.area : "Uttara");
        order.setPostalCode(dto.postalCode != null ? dto.postalCode : "1230");
        order.setDeliveryInstructions(dto.deliveryInstructions);
        order.setPaymentMethod(dto.paymentMethod != null ? dto.paymentMethod : "CASH_ON_DELIVERY");

        String randNum = String.format("%05d", new Random().nextInt(100000));
        order.setOrderNumber("#TS-" + randNum);

        double subtotal = 0.0;
        List<StoreOrderItem> orderItems = new ArrayList<>();

        for (CartItemDTO itemDto : dto.items) {
            ToolStoreProduct product = productRepository.findById(itemDto.productId).orElse(null);
            if (product != null) {
                int qty = itemDto.quantity != null ? itemDto.quantity : 1;
                if (product.getStockQuantity() != null && product.getStockQuantity() >= qty) {
                    product.setStockQuantity(product.getStockQuantity() - qty);
                    productRepository.save(product);
                }

                StoreOrderItem orderItem = new StoreOrderItem(product, qty, product.getPrice());
                orderItem.setOrder(order);
                orderItems.add(orderItem);
                subtotal += orderItem.getSubtotal();
            }
        }

        order.setItems(orderItems);
        order.setSubtotal(subtotal);
        order.setDeliveryFee(60.0);
        order.setDiscount(0.0);
        order.setTotalAmount(subtotal + order.getDeliveryFee() - order.getDiscount());

        if ("CASH_ON_DELIVERY".equalsIgnoreCase(dto.paymentMethod)) {
            order.setPaymentStatus("PENDING");
            order.setOrderStatus("ORDER_PLACED");
        } else {
            order.setPaymentStatus("SUCCESSFUL");
            order.setTransactionId("TXN-" + System.currentTimeMillis());
            order.setOrderStatus("PAYMENT_CONFIRMED");
        }

        return orderRepository.save(order);
    }

    public List<StoreOrder> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByPlacedAtDesc(userId);
    }

    public Optional<StoreOrder> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public List<StoreOrder> getOrdersByBooking(Long bookingId) {
        return orderRepository.findByServiceBookingId(bookingId);
    }

    public List<StoreOrder> getAllOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc();
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

    public StoreOrder updateOrderStatus(Long id, String newStatus, String paymentStatus) {
        StoreOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));

        if (newStatus != null) {
            int currentRank = getStatusRank(order.getOrderStatus());
            int newRank = getStatusRank(newStatus);
            if (newRank < currentRank && newRank != 99) {
                throw new IllegalStateException("Order status cannot be moved backwards from " + order.getOrderStatus() + " to " + newStatus);
            }
            order.setOrderStatus(newStatus);
            if ("DELIVERED".equalsIgnoreCase(newStatus)) {
                order.setPaymentStatus("SUCCESSFUL");
            }
        }
        if (paymentStatus != null) {
            order.setPaymentStatus(paymentStatus);
        }
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public StoreOrder cancelOrder(Long id) {
        StoreOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));

        int currentRank = getStatusRank(order.getOrderStatus());
        if (currentRank >= 6) {
            throw new IllegalStateException("Order cannot be cancelled once it is out for delivery or delivered.");
        }

        order.setOrderStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());

        if (order.getItems() != null) {
            for (StoreOrderItem item : order.getItems()) {
                if (item.getProduct() != null) {
                    ToolStoreProduct product = item.getProduct();
                    int currentQty = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
                    product.setStockQuantity(currentQty + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        return orderRepository.save(order);
    }

    public StoreOrder verifyPayment(String orderNumber, String transactionId) {
        StoreOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderNumber));

        order.setPaymentStatus("SUCCESSFUL");
        order.setOrderStatus("PAYMENT_CONFIRMED");
        order.setTransactionId(transactionId != null ? transactionId : "MOCK-TXN-" + System.currentTimeMillis());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<ProductReview> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public List<ProductReview> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public ProductReview submitReview(ReviewDTO dto) {
        ToolStoreProduct product = productRepository.findById(dto.productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + dto.productId));
        User user = userRepository.findById(dto.userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + dto.userId));

        boolean isDeliveredPurchase = orderRepository.findByUserIdOrderByPlacedAtDesc(dto.userId).stream()
                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getOrderStatus()))
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(dto.productId));

        if (!isDeliveredPurchase) {
            throw new IllegalStateException("You can only submit a review after the product has been delivered to you.");
        }

        if (reviewRepository.existsByProductIdAndUserId(dto.productId, dto.userId)) {
            throw new IllegalStateException("You have already submitted a review for this product. Multiple reviews or edits are not allowed.");
        }

        ProductReview review = new ProductReview(product, user, dto.rating, dto.comment, true);
        if (dto.photoUrl != null && !dto.photoUrl.trim().isEmpty()) {
            review.setPhotoUrl(dto.photoUrl.trim());
        }
        reviewRepository.save(review);

        List<ProductReview> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(dto.productId);
        double avg = reviews.stream().mapToInt(ProductReview::getRating).average().orElse(5.0);
        product.setRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(reviews.size());
        productRepository.save(product);

        return review;
    }

    public Map<String, Object> getAnalytics() {
        List<ToolStoreProduct> products = productRepository.findAll();
        List<StoreOrder> orders = orderRepository.findAll();

        long totalProducts = products.size();
        long activeProducts = products.stream().filter(ToolStoreProduct::isAvailable).count();
        long outOfStock = products.stream().filter(p -> p.getStockQuantity() == null || p.getStockQuantity() == 0).count();
        long lowStock = products.stream().filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0 && p.getStockQuantity() <= p.getLowStockThreshold()).count();

        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> "ORDER_PLACED".equals(o.getOrderStatus()) || "PROCESSING".equals(o.getOrderStatus())).count();
        long completedOrders = orders.stream().filter(o -> "DELIVERED".equals(o.getOrderStatus())).count();

        double totalSales = orders.stream()
                .filter(o -> !"CANCELLED".equals(o.getOrderStatus()) && !"REFUNDED".equals(o.getPaymentStatus()))
                .mapToDouble(StoreOrder::getTotalAmount)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("totalProducts", totalProducts);
        response.put("activeProducts", activeProducts);
        response.put("outOfStock", outOfStock);
        response.put("lowStock", lowStock);
        response.put("totalOrders", totalOrders);
        response.put("pendingOrders", pendingOrders);
        response.put("completedOrders", completedOrders);
        response.put("totalSales", totalSales);

        return response;
    }
}
