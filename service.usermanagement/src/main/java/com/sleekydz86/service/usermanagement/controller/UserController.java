package com.sleekydz86.service.usermanagement.controller;

import com.sleekydz86.service.usermanagement.entity.User;
import com.sleekydz86.service.usermanagement.service.user.UserManagementService;
import com.sleekydz86.service.usermanagement.service.cache.CacheService;
import com.sleekydz86.service.usermanagement.util.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserManagementService userManagementService;
    private final CacheService cacheService;

    public UserController(UserManagementService userManagementService, CacheService cacheService) {
        this.userManagementService = userManagementService;
        this.cacheService = cacheService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = cacheService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userManagementService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userManagementService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        String userId = UserContext.getUserId();
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Long userIdLong = Long.parseLong(userId);
            User user = cacheService.getUser(userIdLong);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (NumberFormatException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
