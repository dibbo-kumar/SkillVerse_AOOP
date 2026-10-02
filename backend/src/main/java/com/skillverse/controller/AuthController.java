package com.skillverse.controller;

import com.skillverse.model.User;
import com.skillverse.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for User Authentication & Profile Management.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Standard CRUD: Save / Register user with RequestBody
     */
    @PostMapping("/register")
    public ResponseEntity<?> save(@RequestBody User user) {
        if (authService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        User saved = authService.registerUser(user);
        return ResponseEntity.ok(saved);
    }

    /**
     * User Login endpoint using RequestBody
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = authService.loginUser(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.status(401).body("Error: Invalid email or password");
    }

    /**
     * Standard CRUD: Get User by ID with PathVariable
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return authService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard List endpoint with optional RequestParam
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAll(@RequestParam(required = false) String role) {
        List<User> list = authService.getAllUsers();
        if (role != null && !role.trim().isEmpty()) {
            list = list.stream().filter(u -> role.equalsIgnoreCase(u.getRole())).toList();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Standard CRUD: Update User by ID with PathVariable and RequestBody
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> profileMap) {
        return authService.updateUser(id, profileMap)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Delete User by ID with PathVariable
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (authService.deleteUser(id)) {
            return ResponseEntity.ok(Map.of("message", "User #" + id + " deleted successfully."));
        }
        return ResponseEntity.notFound().build();
    }
}
