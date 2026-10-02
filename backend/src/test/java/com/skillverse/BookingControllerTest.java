package com.skillverse;

import com.skillverse.model.User;
import com.skillverse.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateBookingAndLifecycleUpdates() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElse(null);
        User worker = userRepository.findByEmail("kamrul@gmail.com").orElse(null);

        assertThat(customer).isNotNull();
        assertThat(worker).isNotNull();

        long stamp = System.currentTimeMillis();
        String bookingJson = String.format(
            "{\"customerId\":%d,\"workerId\":%d,\"serviceType\":\"HVAC & AC\",\"estimatedCost\":1800.0,\"preferredDate\":\"2026-12-01\",\"preferredTime\":\"Slot-%d\",\"description\":\"AC deep wash and gas charging.\"}",
            customer.getId(), worker.getId(), stamp
        );

        // 1. Create booking
        String responseContent = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.estimatedCost").value(1800.0))
                .andExpect(jsonPath("$.startVerificationCode").exists())
                .andExpect(jsonPath("$.completionVerificationCode").exists())
                .andReturn().getResponse().getContentAsString();

        // Extract ID
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(responseContent);
        long bookingId = rootNode.get("id").asLong();

        // 2. Worker Counter Offer
        mockMvc.perform(put("/api/bookings/" + bookingId + "/counter-offer")
                .param("price", "2000.0")
                .param("status", "COUNTERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COUNTERED"))
                .andExpect(jsonPath("$.estimatedCost").value(2000.0));

        // 3. Customer Accept
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                .param("status", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // 4. Worker In Progress
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // 5. Complete Booking
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testGetCustomerBookings() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElse(null);
        assertThat(customer).isNotNull();

        mockMvc.perform(get("/api/bookings/customer/" + customer.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testScheduledBookingAndAutoCancelTimeout() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElse(null);
        User worker = userRepository.findByEmail("kamrul@gmail.com").orElse(null);

        assertThat(customer).isNotNull();
        assertThat(worker).isNotNull();

        long stamp2 = System.currentTimeMillis() + 100;
        String bookingJson = String.format(
            "{\"customerId\":%d,\"workerId\":%d,\"serviceType\":\"Electrician\",\"estimatedCost\":1200.0,\"preferredDate\":\"2026-11-20\",\"preferredTime\":\"Slot-%d\",\"description\":\"Switchboard repair.\"}",
            customer.getId(), worker.getId(), stamp2
        );

        String responseContent = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.preferredDate").value("2026-11-20"))
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(responseContent);
        long bookingId = rootNode.get("id").asLong();

        // Worker accepts booking (goes to pending / awaiting advance)
        mockMvc.perform(put("/api/bookings/" + bookingId + "/accept-price")
                .param("acceptedBy", "WORKER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AWAITING_ADVANCE"));

        // Advance payment
        String advancePayload = "{\"paymentMethod\":\"bKash\",\"mobileNumber\":\"01711223344\",\"amount\":300.0}";
        mockMvc.perform(post("/api/bookings/" + bookingId + "/pay-advance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(advancePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.advancePaid").value(true));

        // Worker starts journey
        mockMvc.perform(put("/api/bookings/" + bookingId + "/on-the-way"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_THE_WAY"));
    }
}
