
package com.example.demo.service;

public interface UserService {
    void addUser(String name, String surname);

    void removeUser(long id);

    void getUser(long id);

    void editUser(long id, String newName, String newSurname);
}