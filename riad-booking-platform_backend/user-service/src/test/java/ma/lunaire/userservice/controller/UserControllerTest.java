package ma.lunaire.userservice.controller;

import ma.lunaire.userservice.dto.UserRequest;
import ma.lunaire.userservice.dto.UserResponse;
import ma.lunaire.userservice.exception.UserNotFoundException;
import ma.lunaire.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse testUserResponse;
    private UserRequest testUserRequest;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserResponse = UserResponse.builder()
                .id(testUserId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .phoneNumber("+1234567890")
                .address("123 Test Street")
                .city("Test City")
                .role("USER")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUserRequest = UserRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .address("123 Test Street")
                .city("Test City")
                .build();
    }

    @Nested
    @DisplayName("Health Endpoint Tests")
    class HealthEndpointTests {

        @Test
        @DisplayName("Should return health status")
        void shouldReturnHealthStatus() {
            ResponseEntity<String> response = userController.health();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User Service is running", response.getBody());
        }
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            when(userService.createUser(any(UserRequest.class))).thenReturn(testUserResponse);

            ResponseEntity<UserResponse> response = userController.createUser(testUserRequest);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testUserResponse.getEmail(), response.getBody().getEmail());
            assertEquals(testUserResponse.getFirstName(), response.getBody().getFirstName());
            verify(userService).createUser(any(UserRequest.class));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by ID")
        void shouldGetUserById() {
            when(userService.getUserById(testUserId)).thenReturn(testUserResponse);

            ResponseEntity<UserResponse> response = userController.getUserById(testUserId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testUserId, response.getBody().getId());
            assertEquals(testUserResponse.getEmail(), response.getBody().getEmail());
            verify(userService).getUserById(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userService.getUserById(testUserId)).thenThrow(new UserNotFoundException("User not found"));

            assertThrows(UserNotFoundException.class, () -> userController.getUserById(testUserId));
            verify(userService).getUserById(testUserId);
        }

        @Test
        @DisplayName("Should get user by email")
        void shouldGetUserByEmail() {
            when(userService.getUserByEmail("test@example.com")).thenReturn(testUserResponse);

            ResponseEntity<UserResponse> response = userController.getUserByEmail("test@example.com");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testUserResponse.getEmail(), response.getBody().getEmail());
            verify(userService).getUserByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should get all users")
        void shouldGetAllUsers() {
            List<UserResponse> users = Arrays.asList(testUserResponse);
            when(userService.getAllUsers()).thenReturn(users);

            ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            assertEquals(testUserResponse.getEmail(), response.getBody().get(0).getEmail());
            verify(userService).getAllUsers();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            when(userService.updateUser(eq(testUserId), any(UserRequest.class))).thenReturn(testUserResponse);

            ResponseEntity<UserResponse> response = userController.updateUser(testUserId, testUserRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testUserResponse.getEmail(), response.getBody().getEmail());
            verify(userService).updateUser(eq(testUserId), any(UserRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            when(userService.updateUser(eq(testUserId), any(UserRequest.class)))
                    .thenThrow(new UserNotFoundException("User not found"));

            assertThrows(UserNotFoundException.class,
                    () -> userController.updateUser(testUserId, testUserRequest));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            doNothing().when(userService).deleteUser(testUserId);

            ResponseEntity<Void> response = userController.deleteUser(testUserId);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(userService).deleteUser(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(testUserId);

            assertThrows(UserNotFoundException.class, () -> userController.deleteUser(testUserId));
            verify(userService).deleteUser(testUserId);
        }
    }
}
