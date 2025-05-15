
package com.example.demo.repo;

import com.example.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FakeRepoTests {

    private FakeRepo fakeRepo;

    @BeforeEach
    public void setUp() {
        fakeRepo = new FakeRepo();
    }

    @Test
    public void testInsertUser() {
        String result = fakeRepo.insertUser(1L, "John", "Doe");
        assertThat(result).isEqualTo("John");
    }

    @Test
    public void testInsertUserWithNullName() {
        String result = fakeRepo.insertUser(1L, null, "Doe");
        assertThat(result).isEqualTo("Invalid user data");
    }

    @Test
    public void testInsertUserWithNullSurname() {
        String result = fakeRepo.insertUser(1L, "John", null);
        assertThat(result).isEqualTo("Invalid user data");
    }

    @Test
    public void testInsertDuplicateUser() {
        fakeRepo.insertUser(1L, "John", "Doe");
        String result = fakeRepo.insertUser(1L, "Jane", "Smith");
        assertThat(result).isEqualTo("User with ID 1 already exists");
    }

    @Test
    public void testFindUserById() {
        fakeRepo.insertUser(1L, "John", "Doe");
        String result = fakeRepo.findUserById(1L);
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    public void testFindUserByIdNotFound() {
        String result = fakeRepo.findUserById(99L);
        assertThat(result).isEqualTo("User not found");
    }

    @Test
    public void testDeleteUser() {
        fakeRepo.insertUser(1L, "John", "Doe");
        String result = fakeRepo.deleteUser(1L);
        assertThat(result).isEqualTo("John");
    }

    @Test
    public void testDeleteUserNotFound() {
        String result = fakeRepo.deleteUser(99L);
        assertThat(result).isEqualTo("User not found");
    }

    @Test
    public void testGetAllUsers() {
        fakeRepo.insertUser(1L, "John", "Doe");
        fakeRepo.insertUser(2L, "Jane", "Smith");
        
        List<User> users = fakeRepo.getAllUsers();
        
        assertThat(users).hasSize(2);
        assertThat(users).extracting("name").contains("John", "Jane");
        assertThat(users).extracting("surname").contains("Doe", "Smith");
    }

    @Test
    public void testGetAllUsersEmptyList() {
        List<User> users = fakeRepo.getAllUsers();
        assertThat(users).isEmpty();
    }
}