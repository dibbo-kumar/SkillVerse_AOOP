package com.skillverse.controller;

import com.skillverse.dto.ServiceBookingRequest;
import com.skillverse.model.ServiceBooking;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/service-bookings")
@CrossOrigin(origins = "*")
public class ServiceBookingController {

    // In-memory list to store service bookings (No Database)
    private List<ServiceBooking> bookingList = new ArrayList<>();
    private Long nextId = 1L;

    public ServiceBookingController() {
        // Sample starter data
        ServiceBooking b1 = new ServiceBooking(nextId++, 1L, 2L, "AC Servicing", "Dhanmondi, Dhaka", 1200.0);
        b1.setStatus("CONFIRMED");
        b1.setPreferredDate("2026-10-01");
        b1.setPreferredTime("11:00 AM");
        bookingList.add(b1);
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<ServiceBooking> getAll(@RequestParam(required = false) String status) {
        if (status == null || status.isEmpty()) {
            return bookingList;
        }

        List<ServiceBooking> result = new ArrayList<>();
        for (ServiceBooking booking : bookingList) {
            if (booking.getStatus() != null && booking.getStatus().equalsIgnoreCase(status)) {
                result.add(booking);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public ServiceBooking getById(@PathVariable Long id) {
        for (ServiceBooking booking : bookingList) {
            if (booking.getId().equals(id)) {
                return booking;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with ServiceBookingRequest object
    @PostMapping
    public ServiceBooking save(@RequestBody ServiceBookingRequest request) {
        ServiceBooking booking = new ServiceBooking();
        booking.setId(nextId++);
        booking.setCustomerId(request.getCustomerId());
        booking.setWorkerId(request.getWorkerId());
        booking.setServiceType(request.getServiceType());
        booking.setPreferredDate(request.getPreferredDate());
        booking.setPreferredTime(request.getPreferredTime());
        booking.setAddress(request.getAddress());
        booking.setDescription(request.getDescription());
        booking.setEstimatedCost(request.getEstimatedCost());
        booking.setAgreedCost(request.getEstimatedCost());

        bookingList.add(booking);
        return booking;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public ServiceBooking upload(@PathVariable Long id, @RequestParam String beforePhoto) {
        for (ServiceBooking booking : bookingList) {
            if (booking.getId().equals(id)) {
                booking.setBeforePhoto(beforePhoto);
                return booking;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < bookingList.size(); i++) {
            if (bookingList.get(i).getId().equals(id)) {
                bookingList.remove(i);
                return "ServiceBooking deleted successfully";
            }
        }
        return "ServiceBooking not found";
    }
}
