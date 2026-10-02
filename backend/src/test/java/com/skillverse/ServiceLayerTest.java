package com.skillverse;

import com.skillverse.dto.BookingRequest;
import com.skillverse.model.MarketplaceItem;
import com.skillverse.model.ServiceBooking;
import com.skillverse.model.User;
import com.skillverse.repository.UserRepository;
import com.skillverse.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verification test verifying the standard architectural flow:
 * User -> Controller -> Service -> Repository -> Model (DB Entity)
 */
@SpringBootTest
public class ServiceLayerTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ToolStoreService toolStoreService;

    @Autowired
    private AIService aiService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAuthServiceLayer() {
        List<User> users = authService.getAllUsers();
        assertThat(users).isNotEmpty();

        Optional<User> userOpt = authService.findByEmail("anis@gmail.com");
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getName()).isEqualTo("Anisur Rahman");
    }

    @Test
    void testAIServiceDiagnostics() {
        Map<String, Object> acEstimate = aiService.estimateCost("AC is not cooling and leaking water");
        assertThat(acEstimate.get("baseServiceCost")).isNotNull();
        assertThat(acEstimate.get("totalEstimatedCost")).isNotNull();

        Map<String, String> chatRes = aiService.chatbotResponse("Hello, I need help with AC");
        assertThat(chatRes.get("response")).contains("HVAC");
    }

    @Test
    void testMarketplaceServiceLayer() {
        MarketplaceItem item = new MarketplaceItem();
        item.setTitle("Test Drill Machine");
        item.setDescription("Heavy duty drill");
        item.setPrice(2500.0);
        item.setType("TOOL");

        MarketplaceItem saved = marketplaceService.save(item);
        assertThat(saved.getId()).isNotNull();

        Optional<MarketplaceItem> fetched = marketplaceService.getById(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getTitle()).isEqualTo("Test Drill Machine");

        boolean deleted = marketplaceService.delete(saved.getId());
        assertThat(deleted).isTrue();
    }

    @Test
    void testToolStoreServiceLayer() {
        var products = toolStoreService.getProducts(null, null, null, null, null);
        assertThat(products).isNotEmpty();

        var categories = toolStoreService.getCategories();
        assertThat(categories).isNotNull();

        var analytics = toolStoreService.getAnalytics();
        assertThat(analytics.get("totalProducts")).isNotNull();
    }

    @Test
    void testBookingServiceDirectFlow() {
        User customer = userRepository.findByEmail("anis@gmail.com").orElseThrow();
        User worker = userRepository.findByEmail("kamrul@gmail.com").orElseThrow();

        BookingRequest req = new BookingRequest();
        req.setCustomerId(customer.getId());
        req.setWorkerId(worker.getId());
        req.setServiceType("Electrical");
        req.setEstimatedCost(1200.0);
        req.setDescription("Switchboard short circuit repair");

        ServiceBooking booking = bookingService.save(req);
        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo("PENDING");
        assertThat(booking.getStartVerificationCode()).isNotNull();

        // Counter offer through service
        ServiceBooking countered = bookingService.counterOffer(booking.getId(), 1500.0, "WORKER", "COUNTERED");
        assertThat(countered.getStatus()).isEqualTo("COUNTERED");
        assertThat(countered.getEstimatedCost()).isEqualTo(1500.0);

        // Delete cleanup
        boolean deleted = bookingService.delete(booking.getId());
        assertThat(deleted).isTrue();
    }
}
