
package com.example.demo.repo;

import com.example.demo.model.User;

public interface FakeRepoInterface {
    String insertUser(long id, String name, String surname);
    String findUserById(long id);
    String deleteUser(long id);
}