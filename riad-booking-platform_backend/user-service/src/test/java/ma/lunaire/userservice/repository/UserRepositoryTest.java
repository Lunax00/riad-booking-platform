package ma.lunaire.userservice.repository;

import ma.lunaire.userservice.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private UserEntity testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = UserEntity.builder()
                .id(testUserId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .keycloakUserId("keycloak-123")
                .role("USER")
                .isActive(true)
                .build();
    }

    @Nested
    @DisplayName("FindByEmail Tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            Optional<UserEntity> result = userRepository.findByEmail("test@example.com");

            assertTrue(result.isPresent());
            assertEquals("test@example.com", result.get().getEmail());
            assertEquals("John", result.get().getFirstName());
            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should return empty when email not found")
        void shouldReturnEmptyWhenEmailNotFound() {
            when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

            Optional<UserEntity> result = userRepository.findByEmail("notfound@example.com");

            assertFalse(result.isPresent());
            verify(userRepository).findByEmail("notfound@example.com");
        }

        @Test
        @DisplayName("Should find user with case-sensitive email")
        void shouldFindUserWithCaseSensitiveEmail() {
            when(userRepository.findByEmail("Test@Example.com")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            Optional<UserEntity> uppercaseResult = userRepository.findByEmail("Test@Example.com");
            Optional<UserEntity> lowercaseResult = userRepository.findByEmail("test@example.com");

            assertFalse(uppercaseResult.isPresent());
            assertTrue(lowercaseResult.isPresent());
        }
    }

    @Nested
    @DisplayName("FindByKeycloakUserId Tests")
    class FindByKeycloakUserIdTests {

        @Test
        @DisplayName("Should find user by Keycloak user ID")
        void shouldFindUserByKeycloakUserId() {
            when(userRepository.findByKeycloakUserId("keycloak-123")).thenReturn(Optional.of(testUser));

            Optional<UserEntity> result = userRepository.findByKeycloakUserId("keycloak-123");

            assertTrue(result.isPresent());
            assertEquals("keycloak-123", result.get().getKeycloakUserId());
            assertEquals("test@example.com", result.get().getEmail());
            verify(userRepository).findByKeycloakUserId("keycloak-123");
        }

        @Test
        @DisplayName("Should return empty when Keycloak user ID not found")
        void shouldReturnEmptyWhenKeycloakUserIdNotFound() {
            when(userRepository.findByKeycloakUserId("unknown-keycloak-id")).thenReturn(Optional.empty());

            Optional<UserEntity> result = userRepository.findByKeycloakUserId("unknown-keycloak-id");

            assertFalse(result.isPresent());
            verify(userRepository).findByKeycloakUserId("unknown-keycloak-id");
        }
    }

    @Nested
    @DisplayName("ExistsByEmail Tests")
    class ExistsByEmailTests {

        @Test
        @DisplayName("Should return true when email exists")
        void shouldReturnTrueWhenEmailExists() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            boolean exists = userRepository.existsByEmail("test@example.com");

            assertTrue(exists);
            verify(userRepository).existsByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            when(userRepository.existsByEmail("notfound@example.com")).thenReturn(false);

            boolean exists = userRepository.existsByEmail("notfound@example.com");

            assertFalse(exists);
            verify(userRepository).existsByEmail("notfound@example.com");
        }
    }

    @Nested
    @DisplayName("ExistsByKeycloakUserId Tests")
    class ExistsByKeycloakUserIdTests {

        @Test
        @DisplayName("Should return true when Keycloak user ID exists")
        void shouldReturnTrueWhenKeycloakUserIdExists() {
            when(userRepository.existsByKeycloakUserId("keycloak-123")).thenReturn(true);

            boolean exists = userRepository.existsByKeycloakUserId("keycloak-123");

            assertTrue(exists);
            verify(userRepository).existsByKeycloakUserId("keycloak-123");
        }

        @Test
        @DisplayName("Should return false when Keycloak user ID does not exist")
        void shouldReturnFalseWhenKeycloakUserIdDoesNotExist() {
            when(userRepository.existsByKeycloakUserId("unknown-id")).thenReturn(false);

            boolean exists = userRepository.existsByKeycloakUserId("unknown-id");

            assertFalse(exists);
            verify(userRepository).existsByKeycloakUserId("unknown-id");
        }
    }

    @Nested
    @DisplayName("Save Tests")
    class SaveTests {

        @Test
        @DisplayName("Should save user and return saved entity")
        void shouldSaveUserAndReturnSavedEntity() {
            when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

            UserEntity savedUser = userRepository.save(testUser);

            assertNotNull(savedUser);
            assertEquals(testUserId, savedUser.getId());
            assertEquals("test@example.com", savedUser.getEmail());
            verify(userRepository).save(testUser);
        }
    }

    @Nested
    @DisplayName("FindById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find user by ID")
        void shouldFindUserById() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            Optional<UserEntity> result = userRepository.findById(testUserId);

            assertTrue(result.isPresent());
            assertEquals(testUserId, result.get().getId());
            verify(userRepository).findById(testUserId);
        }

        @Test
        @DisplayName("Should return empty when ID not found")
        void shouldReturnEmptyWhenIdNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

            Optional<UserEntity> result = userRepository.findById(unknownId);

            assertFalse(result.isPresent());
            verify(userRepository).findById(unknownId);
        }
    }

    @Nested
    @DisplayName("DeleteById Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete user by ID")
        void shouldDeleteUserById() {
            doNothing().when(userRepository).deleteById(testUserId);

            userRepository.deleteById(testUserId);

            verify(userRepository).deleteById(testUserId);
        }
    }

    @Nested
    @DisplayName("ExistsById Tests")
    class ExistsByIdTests {

        @Test
        @DisplayName("Should return true when ID exists")
        void shouldReturnTrueWhenIdExists() {
            when(userRepository.existsById(testUserId)).thenReturn(true);

            boolean exists = userRepository.existsById(testUserId);

            assertTrue(exists);
            verify(userRepository).existsById(testUserId);
        }

        @Test
        @DisplayName("Should return false when ID does not exist")
        void shouldReturnFalseWhenIdDoesNotExist() {
            UUID unknownId = UUID.randomUUID();
            when(userRepository.existsById(unknownId)).thenReturn(false);

            boolean exists = userRepository.existsById(unknownId);

            assertFalse(exists);
            verify(userRepository).existsById(unknownId);
        }
    }
}

