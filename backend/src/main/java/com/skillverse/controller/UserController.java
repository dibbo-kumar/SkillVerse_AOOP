package com.skillverse.controller;

import com.skillverse.dto.UserRequest;
import com.skillverse.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    // In-memory list to store users (No Database)
    private List<User> userList = new ArrayList<>();
    private Long nextId = 1L;

    public UserController() {
        // Sample starter data
        userList.add(new User(nextId++, "Rahim Ahmed", "rahim@example.com", "01711112233", "CUSTOMER", "Dhanmondi, Dhaka"));
        userList.add(new User(nextId++, "Karim Electrician", "karim@example.com", "01822223344", "WORKER", "Uttara, Dhaka"));
        userList.add(new User(nextId++, "Admin User", "admin@skillverse.com", "01933334455", "ADMIN", "Gulshan, Dhaka"));
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<User> getAll(@RequestParam(required = false) String role) {
        if (role == null || role.isEmpty()) {
            return userList;
        }

        List<User> result = new ArrayList<>();
        for (User user : userList) {
            if (user.getRole().equalsIgnoreCase(role)) {
                result.add(user);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with UserRequest object
    @PostMapping
    public User save(@RequestBody UserRequest request) {
        User user = new User();
        user.setId(nextId++);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setNidNumber(request.getNidNumber());
        user.setAddress(request.getAddress());

        userList.add(user);
        return user;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public User upload(@PathVariable Long id, @RequestParam String profilePictureUrl) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                user.setProfilePicture(profilePictureUrl);
                return user;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(id)) {
                userList.remove(i);
                return "User deleted successfully";
            }
        }
        return "User not found";
    }
}
