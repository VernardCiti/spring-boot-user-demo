# Spring Boot User Management System

A comprehensive Spring Boot project demonstrating key concepts including MVC architecture, dependency injection, annotations, in-memory data storage, service logic, and unit testing.

## Project Overview

This repository contains an implementation of the "Intro to Spring Boot – Part 1" project, with improvements to the original requirements. The project focuses on:

- Spring Boot fundamentals (annotations, DI, IoC)
- Layered architecture implementation
- Object-oriented principles (encapsulation, abstraction)
- Clean, testable code following Java and Spring Boot standards
- Git version control best practices

## Improvements Over Original Requirements

The following improvements have been made to enhance the original project guidelines:

1. **Thread-safe repository implementation** - Using ConcurrentHashMap instead of ArrayList for better concurrency handling
2. **Robust ID generation** - AtomicLong for safe ID generation across threads
3. **Better error handling** - Added exception handling and validation
4. **Enhanced user interaction** - Added CommandLineRunner for better terminal interaction
5. **REST Controller** - Added optional REST API endpoints
6. **Data validation** - Input validation to prevent invalid data
7. **Comprehensive testing** - More extensive test cases

## Project Structure

```
spring-boot-user-demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java
│   │   │   ├── model/
│   │   │   │   └── User.java
│   │   │   ├── repo/
│   │   │   │   ├── FakeRepoInterface.java
│   │   │   │   └── FakeRepo.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   └── UserServiceImpl.java
│   │   │   ├── controller/ (optional)
│   │   │   │   └── UserController.java
│   │   │   └── cli/
│   │   │       └── UserCLIRunner.java
│   └── test/
│       └── java/com/example/demo/
│           ├── service/
│           │   └── UserServiceTests.java
│           └── repo/
│               └── FakeRepoTests.java
├── build.gradle
└── README.md
```

## Implementation Details

### Key Files and Their Improvements

#### 1. User Model (`User.java`)
- Added validation for name and surname
- Added proper equals() and hashCode() methods
- Added toString() for better debugging

#### 2. Repository Implementation (`FakeRepo.java`)
- Used ConcurrentHashMap instead of ArrayList for thread safety
- Better error messages and handling
- Added methods to get all users

#### 3. Service Implementation (`UserServiceImpl.java`)
- Used AtomicLong for thread-safe ID generation
- Added validation before processing
- More descriptive console messages
- Better error handling

#### 4. CLI Runner (`UserCLIRunner.java`) - New Addition
- Interactive command-line interface
- Better user experience for terminal execution
- Command help and examples

#### 5. Controller (`UserController.java`) - Optional
- REST API endpoints for CRUD operations
- Proper HTTP status codes
- Request validation

## How to Run

1. Clone the repository:
```
git clone https://github.com/VernardCiti/spring-boot-user-demo.git
cd spring-boot-user-demo
```

2. Build the project:
```
./gradlew build
```

3. Run the application:
```
./gradlew bootRun
```

4. Interact with the CLI:
- Add user: `add John Doe`
- Get user: `get 1`
- Remove user: `remove 1`
- List all users: `list`
- Exit: `exit`

## How to Test

Run the tests with:
```
./gradlew test
```

## Git Workflow

This project follows the Gitflow workflow:

- `main`: Stable, production-ready code
- `develop`: Integration branch for new features
- `feature/*`: Individual feature branches
- `bugfix/*`: Bug fix branches
- `test`: Testing before merging to develop

### Commit Message Format

```
<type>: <short description>

<optional detailed description>
```

Types include:
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code change that neither fixes a bug nor adds a feature
- `test`: Adding or modifying tests
- `docs`: Documentation only changes
- `style`: Changes that do not affect the meaning of the code
- `chore`: Changes to the build process or auxiliary tools

## Key Implementations

### 1. Improved Repository with Thread Safety

```java
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
    
    // New method
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
```

### 2. Enhanced Service Implementation

```java
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
    
    // New method
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
```

### 3. New CLI Runner

```java
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
            String input = scanner.nextLine().trim();
            processCommand(input);
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
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be a number");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("  add <name> <surname> - Add a new user");
        System.out.println("  get <id> - Get user by ID");
        System.out.println("  remove <id> - Remove user by ID");
        System.out.println("  list - List all users");
        System.out.println("  help - Show this help");
        System.out.println("  exit - Exit the application");
    }
}
```

## Conclusion

This enhanced Spring Boot project demonstrates fundamental Spring concepts with improvements for better robustness, thread safety, and user experience. The implementation follows Spring best practices and provides a solid foundation for understanding Spring Boot architecture.

## References

- [Spring Framework Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/)
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Baeldung Spring Boot Testing Guide](https://www.baeldung.com/spring-boot-testing)
- [Spring Framework Dependency Injection Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-dependencies)
