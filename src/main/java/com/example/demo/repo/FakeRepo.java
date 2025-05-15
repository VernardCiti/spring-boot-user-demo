
package com.example.demo.repo;

import com.example.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FakeRepo implements FakeRepoInterface {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public String insertUser(long id, String name, String surname) {
        if (name == null || surname == null) {
            return "Invalid user data";
        }
        
        User user = new User(id, name, surname);
        User previous = users.putIfAbsent(id, user);
        
        if (previous != null) {
            return "User with ID " + id + " already exists";
        }
        
        return name;
    }

    @Override
    public String findUserById(long id) {
        User user = users.get(id);
        if (user == null) {
            return "User not found";
        }
        return user.getName() + " " + user.getSurname();
    }

    @Override
    public String deleteUser(long id) {
        User user = users.remove(id);
        if (user == null) {
            return "User not found";
        }
        return user.getName();
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}