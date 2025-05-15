
package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.FakeRepo;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final FakeRepo fakeRepo;
    private boolean running = true;
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public UserController(UserService userService, FakeRepo fakeRepo) {
        this.userService = userService;
        this.fakeRepo = fakeRepo;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestParam String name, @RequestParam String surname) {
        Map<String, Object> response = new HashMap<>();
        
        if (name == null || name.trim().isEmpty() || surname == null || surname.trim().isEmpty()) {
            response.put("error", "Name and surname cannot be empty");
            return ResponseEntity.badRequest().body(response);
        }
        
        userService.addUser(name, surname);
        response.put("message", name + " added successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable long id) {
        Map<String, Object> response = new HashMap<>();
        
        String result = fakeRepo.findUserById(id);
        if (result.equals("User not found")) {
            response.put("error", "User not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        response.put("fullName", result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> removeUser(@PathVariable long id) {
        Map<String, Object> response = new HashMap<>();
        
        String result = fakeRepo.deleteUser(id);
        if (result.equals("User not found")) {
            response.put("error", "User not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        response.put("message", result + " removed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = fakeRepo.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editUser(
            @PathVariable long id,
            @RequestParam String newName,
            @RequestParam String newSurname) {
        Map<String, Object> response = new HashMap<>();

        if (newName == null || newName.trim().isEmpty() || newSurname == null || newSurname.trim().isEmpty()) {
            response.put("error", "Name and surname cannot be empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            userService.editUser(id, newName, newSurname);
            response.put("message", "User with ID " + id + " updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public void printUserDetails(long id) {
        if (id <= 0) {
            System.out.println("Error: Invalid ID");
            return;
        }

        String result = fakeRepo.findUserById(id);
        if (!result.equals("User not found")) {
            System.out.println("User found: " + result);
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    private void processCommand(String input) {
        if (input.isEmpty()) {
            return;
        }

        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "add":
                    if (parts.length < 3) {
                        System.out.println("Usage: add <name> <surname>");
                    } else {
                        userService.addUser(parts[1], parts[2]);
                    }
                    break;
                case "get":
                    if (parts.length < 2) {
                        System.out.println("Usage: get <id>");
                    } else {
                        try {
                            long id = Long.parseLong(parts[1]);
                            userService.getUser(id);
                        } catch (NumberFormatException e) {
                            System.out.println("Error: ID must be a number");
                        }
                    }
                    break;
                case "remove":
                    if (parts.length < 2) {
                        System.out.println("Usage: remove <id>");
                    } else {
                        try {
                            long id = Long.parseLong(parts[1]);
                            userService.removeUser(id);
                        } catch (NumberFormatException e) {
                            System.out.println("Error: ID must be a number");
                        }
                    }
                    break;
                case "edit":
                    if (parts.length < 4) {
                        System.out.println("Usage: edit <id> <newName> <newSurname>");
                    } else {
                        try {
                            long id = Long.parseLong(parts[1]);
                            userService.editUser(id, parts[2], parts[3]);
                        } catch (NumberFormatException e) {
                            System.out.println("Error: ID must be a number");
                        }
                    }
                    break;
                case "list":
                    if (userService instanceof UserServiceImpl) {
                        ((UserServiceImpl) userService).listAllUsers();
                    }
                    break;
                case "help":
                    printHelp();
                    break;
                case "exit":
                    System.out.println("Exiting application...");
                    running = false;
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Welcome to User Management System");
        System.out.println("Available commands:");
        System.out.println("  add <name> <surname> - Add a new user");
        System.out.println("  get <id> - Get user by ID");
        System.out.println("  remove <id> - Remove user by ID");
        System.out.println("  edit <id> <newName> <newSurname> - Edit user details");
        System.out.println("  list - List all users");
        System.out.println("  help - Show this help");
        System.out.println("  exit - Exit the application");
        System.out.print("> ");
    }
}