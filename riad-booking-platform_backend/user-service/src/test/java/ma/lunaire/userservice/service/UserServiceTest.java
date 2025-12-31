package ma.lunaire.userservice.service;

import ma.lunaire.userservice.dto.UserRequest;
import ma.lunaire.userservice.dto.UserResponse;
import ma.lunaire.userservice.entity.UserEntity;
import ma.lunaire.userservice.exception.UserNotFoundException;
import ma.lunaire.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private UserRequest testUserRequest;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = UserEntity.builder()
                .id(testUserId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .address("123 Test Street")
                .city("Test City")
                .profilePictureUrl("http://example.com/pic.jpg")
                .bio("Test bio")
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
                .profilePictureUrl("http://example.com/pic.jpg")
                .bio("Test bio")
                .build();
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            when(userRepository.existsByEmail(testUserRequest.getEmail())).thenReturn(false);
            when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

            UserResponse result = userService.createUser(testUserRequest);

            assertNotNull(result);
            assertEquals(testUser.getEmail(), result.getEmail());
            assertEquals(testUser.getFirstName(), result.getFirstName());
            assertEquals(testUser.getLastName(), result.getLastName());
            verify(userRepository).existsByEmail(testUserRequest.getEmail());
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(userRepository.existsByEmail(testUserRequest.getEmail())).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> userService.createUser(testUserRequest));

            verify(userRepository).existsByEmail(testUserRequest.getEmail());
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by ID successfully")
        void shouldGetUserByIdSuccessfully() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            UserResponse result = userService.getUserById(testUserId);

            assertNotNull(result);
            assertEquals(testUser.getId(), result.getId());
            assertEquals(testUser.getEmail(), result.getEmail());
            verify(userRepository).findById(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void shouldThrowExceptionWhenUserNotFoundById() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserById(testUserId));

            verify(userRepository).findById(testUserId);
        }

        @Test
        @DisplayName("Should get user by email successfully")
        void shouldGetUserByEmailSuccessfully() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            UserResponse result = userService.getUserByEmail("test@example.com");

            assertNotNull(result);
            assertEquals(testUser.getEmail(), result.getEmail());
            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should throw exception when user not found by email")
        void shouldThrowExceptionWhenUserNotFoundByEmail() {
            when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("notfound@example.com"));

            verify(userRepository).findByEmail("notfound@example.com");
        }

        @Test
        @DisplayName("Should get all users")
        void shouldGetAllUsers() {
            UserEntity user2 = UserEntity.builder()
                    .id(UUID.randomUUID())
                    .email("user2@example.com")
                    .firstName("Jane")
                    .lastName("Doe")
                    .role("USER")
                    .isActive(true)
                    .build();

            when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

            List<UserResponse> result = userService.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            UserRequest updateRequest = UserRequest.builder()
                    .firstName("UpdatedName")
                    .lastName("UpdatedLast")
                    .city("New City")
                    .build();

            UserEntity updatedUser = UserEntity.builder()
                    .id(testUserId)
                    .email(testUser.getEmail())
                    .firstName("UpdatedName")
                    .lastName("UpdatedLast")
                    .city("New City")
                    .role("USER")
                    .isActive(true)
                    .build();

            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

            UserResponse result = userService.updateUser(testUserId, updateRequest);

            assertNotNull(result);
            assertEquals("UpdatedName", result.getFirstName());
            assertEquals("UpdatedLast", result.getLastName());
            assertEquals("New City", result.getCity());
            verify(userRepository).findById(testUserId);
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.updateUser(testUserId, testUserRequest));

            verify(userRepository).findById(testUserId);
            verify(userRepository, never()).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("Should only update non-null fields")
        void shouldOnlyUpdateNonNullFields() {
            UserRequest partialUpdate = UserRequest.builder()
                    .firstName("NewFirstName")
                    .build();

            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.updateUser(testUserId, partialUpdate);

            assertNotNull(result);
            assertEquals("NewFirstName", result.getFirstName());
            assertEquals(testUser.getLastName(), result.getLastName());
            assertEquals(testUser.getCity(), result.getCity());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            when(userRepository.existsById(testUserId)).thenReturn(true);
            doNothing().when(userRepository).deleteById(testUserId);

            assertDoesNotThrow(() -> userService.deleteUser(testUserId));

            verify(userRepository).existsById(testUserId);
            verify(userRepository).deleteById(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            when(userRepository.existsById(testUserId)).thenReturn(false);

            assertThrows(UserNotFoundException.class, () -> userService.deleteUser(testUserId));

            verify(userRepository).existsById(testUserId);
            verify(userRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Link User to Keycloak Tests")
    class LinkUserToKeycloakTests {

        @Test
        @DisplayName("Should link existing user to Keycloak")
        void shouldLinkExistingUserToKeycloak() {
            String keycloakId = "keycloak-user-id";
            when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserEntity result = userService.linkUserToKeycloak(testUser.getEmail(), keycloakId);

            assertNotNull(result);
            assertEquals(keycloakId, result.getKeycloakUserId());
            verify(userRepository).findByEmail(testUser.getEmail());
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("Should create new user and link to Keycloak when user does not exist")
        void shouldCreateNewUserAndLinkToKeycloak() {
            String email = "new@example.com";
            String keycloakId = "keycloak-user-id";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserEntity result = userService.linkUserToKeycloak(email, keycloakId);

            assertNotNull(result);
            assertEquals(email, result.getEmail());
            assertEquals(keycloakId, result.getKeycloakUserId());
            verify(userRepository).findByEmail(email);
            verify(userRepository).save(any(UserEntity.class));
        }
    }
}

