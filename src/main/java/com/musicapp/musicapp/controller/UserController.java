package com.musicapp.musicapp.controller;

import com.musicapp.musicapp.entity.User;
import com.musicapp.musicapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController               // Combines @Controller + @ResponseBody
@RequestMapping("/users")     // All routes in this class start with /users
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /users → Register new user
    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User saved = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /users → Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /users/{id} → Get one user
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}