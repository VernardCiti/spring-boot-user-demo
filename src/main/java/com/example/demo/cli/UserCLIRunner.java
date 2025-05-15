package com.example.demo.cli;

import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * UserCLIRunner is responsible for running the User Management System CLI.
 */
@Component
public class UserCLIRunner implements CommandLineRunner {

    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    @Autowired
    public UserCLIRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Welcome to User Management System");
        System.out.println("Available commands:");
        printHelp();

        while (running) {
            System.out.print("> ");
            try {
                String input = scanner.nextLine().trim();
                processCommand(input);
            } catch (java.util.NoSuchElementException e) {
                System.out.println("No input available. Exiting...");
                running = false;
            }
        }
    }

    private void processCommand(String input) {
        if (input.isEmpty()) {
            printHelp(); // Show help even if no input is provided
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
                    return; // Skip printing help after exit
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Print help after every command
        printHelp();
    }

    private void printHelp() {
        System.out.println("  add <name> <surname> - Add a new user");
        System.out.println("  get <id> - Get user by ID");
        System.out.println("  remove <id> - Remove user by ID");
        System.out.println("  edit <id> <newName> <newSurname> - Edit user details");
        System.out.println("  list - List all users");
        System.out.println("  help - Show this help");
        System.out.println("  exit - Exit the application");
    }
}