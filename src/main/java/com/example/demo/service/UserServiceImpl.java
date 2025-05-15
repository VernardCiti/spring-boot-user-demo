
package com.example.demo.service;

import com.example.demo.repo.FakeRepo;
import com.example.demo.repo.FakeRepoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import com.example.demo.model.User;

@Service
public class UserServiceImpl implements UserService {
    private final FakeRepoInterface fakeRepo;
    private final AtomicLong nextId = new AtomicLong(1); // Thread-safe ID generation

    @Autowired
    public UserServiceImpl(FakeRepoInterface fakeRepo) {
        this.fakeRepo = fakeRepo;
    }

    @Override
    public void addUser(String name, String surname) {
        if (name == null || name.trim().isEmpty() || surname == null || surname.trim().isEmpty()) {
            System.out.println("Error: Name and surname cannot be empty");
            return;
        }

        long id = nextId.getAndIncrement();
        String result = fakeRepo.insertUser(id, name, surname);

        if (result.equals(name)) {
            System.out.println(name + " added with ID: " + id);
        } else {
            System.out.println("Failed to add user: " + result);
        }
    }

    @Override
    public void removeUser(long id) {
        if (id <= 0) {
            System.out.println("Error: Invalid ID");
            return;
        }

        String result = fakeRepo.deleteUser(id);
        if (!result.equals("User not found")) {
            System.out.println(result + " removed successfully");
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    @Override
    public void getUser(long id) {
        if (id <= 0) {
            System.out.println("Error: Invalid ID");
            return;
        }

        String result = fakeRepo.findUserById(id);
        if (!result.equals("User not found")) {
            System.out.println("Hello " + result);
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    @Override
    public void editUser(long id, String newName, String newSurname) {
        if (id <= 0 || newName == null || newName.trim().isEmpty() || newSurname == null
                || newSurname.trim().isEmpty()) {
            System.out.println("Error: Invalid ID or name/surname cannot be empty");
            return;
        }

        if (fakeRepo instanceof FakeRepo) {
            User user = ((FakeRepo) fakeRepo).getAllUsers().stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (user != null) {
                user.setName(newName);
                user.setSurname(newSurname);
                System.out.println("User with ID " + id + " updated successfully");
            } else {
                System.out.println("User not found with ID: " + id);
            }
        } else {
            System.out.println("Operation not supported");
        }
    }

    public void listAllUsers() {
        if (fakeRepo instanceof FakeRepo) {
            List<User> allUsers = ((FakeRepo) fakeRepo).getAllUsers();
            if (allUsers.isEmpty()) {
                System.out.println("No users found");
            } else {
                System.out.println("All users:");
                for (User user : allUsers) {
                    System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + " " + user.getSurname());
                }
            }
        } else {
            System.out.println("Operation not supported");
        }
    }

   
}