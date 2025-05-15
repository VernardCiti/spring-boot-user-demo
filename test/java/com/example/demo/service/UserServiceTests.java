
package com.example.demo.serviceprivate void processCommand(String input) {
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
                    userService.getUser(Long.parseLong(parts[1]));
                }
                break;
            case "remove":
                if (parts.length < 2) {
                    System.out.println("Usage: remove <id>");
                } else {
                    userService.removeUser(Long.parseLong(parts[1]));
                }
                break;
            case "edit":
                if (parts.length < 4) {
                    System.out.println("Usage: edit <id> <newName> <newSurname>");
                } else {
                    userService.editUser(Long.parseLong(parts[1]), parts[2], parts[3]);
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
    } catch (NumberFormatException e) {
        System.out.println("Error: ID must be a number");
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }

    // Print help after every command
    printHelp();
};

import com.example.demo.repo.FakeRepoInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private FakeRepoInterface fakeRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testAddUser() {
        when(fakeRepo.insertUser(anyLong(), eq("John"), eq("Doe"))).thenReturn("John");
        userService.addUser("John", "Doe");
        assertThat(outContent.toString()).contains("John added with ID:");
        verify(fakeRepo).insertUser(anyLong(), eq("John"), eq("Doe"));
    }

    @Test
    public void testAddUserWithEmptyName() {
        userService.addUser("", "Doe");
        assertThat(outContent.toString()).contains("Error: Name and surname cannot be empty");
    }

    @Test
    public void testAddUserWithNullName() {
        userService.addUser(null, "Doe");
        assertThat(outContent.toString()).contains("Error: Name and surname cannot be empty");
    }

    @Test
    public void testRemoveUser() {
        when(fakeRepo.deleteUser(1L)).thenReturn("John");
        userService.removeUser(1L);
        assertThat(outContent.toString()).contains("John removed successfully");
        verify(fakeRepo).deleteUser(1L);
    }

    @Test
    public void testRemoveUserWithInvalidId() {
        userService.removeUser(-1);
        assertThat(outContent.toString()).contains("Error: Invalid ID");
    }

    @Test
    public void testGetUser() {
        when(fakeRepo.findUserById(1L)).thenReturn("John Doe");
        userService.getUser(1L);
        assertThat(outContent.toString()).contains("Hello John Doe");
        verify(fakeRepo).findUserById(1L);
    }

    @Test
    public void testGetUserWithInvalidId() {
        userService.getUser(0);
        assertThat(outContent.toString()).contains("Error: Invalid ID");
    }

    @Test
    public void testRemoveUserNotFound() {
        when(fakeRepo.deleteUser(99L)).thenReturn("User not found");
        userService.removeUser(99L);
        assertThat(outContent.toString()).contains("User not found with ID: 99");
        verify(fakeRepo).deleteUser(99L);
    }

    @Test
    public void testGetUserNotFound() {
        when(fakeRepo.findUserById(99L)).thenReturn("User not found");
        userService.getUser(99L);
        assertThat(outContent.toString()).contains("User not found with ID: 99");
        verify(fakeRepo).findUserById(99L);
    }
}