package ma.lunaire.userservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserEntity Tests")
class UserEntityTest {

    private UUID testId;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testDateTime = LocalDateTime.now();
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build UserEntity with all fields")
        void shouldBuildWithAllFields() {
            UserEntity user = UserEntity.builder()
                    .id(testId)
                    .email("test@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .phoneNumber("+1234567890")
                    .address("123 Test Street")
                    .city("Test City")
                    .keycloakUserId("keycloak-123")
                    .profilePictureUrl("http://example.com/pic.jpg")
                    .bio("Test bio")
                    .role("USER")
                    .isActive(true)
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .build();

            assertNotNull(user);
            assertEquals(testId, user.getId());
            assertEquals("test@example.com", user.getEmail());
            assertEquals("John", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("+1234567890", user.getPhoneNumber());
            assertEquals("123 Test Street", user.getAddress());
            assertEquals("Test City", user.getCity());
            assertEquals("keycloak-123", user.getKeycloakUserId());
            assertEquals("http://example.com/pic.jpg", user.getProfilePictureUrl());
            assertEquals("Test bio", user.getBio());
            assertEquals("USER", user.getRole());
            assertTrue(user.getIsActive());
        }

        @Test
        @DisplayName("Should have default values for role and isActive")
        void shouldHaveDefaultValues() {
            UserEntity user = UserEntity.builder()
                    .email("test@example.com")
                    .build();

            assertEquals("USER", user.getRole());
            assertTrue(user.getIsActive());
        }

        @Test
        @DisplayName("Should override default values when explicitly set")
        void shouldOverrideDefaultValues() {
            UserEntity user = UserEntity.builder()
                    .email("test@example.com")
                    .role("ADMIN")
                    .isActive(false)
                    .build();

            assertEquals("ADMIN", user.getRole());
            assertFalse(user.getIsActive());
        }
    }

    @Nested
    @DisplayName("GetFullName Tests")
    class GetFullNameTests {

        @Test
        @DisplayName("Should return full name when both first and last name exist")
        void shouldReturnFullNameWhenBothNamesExist() {
            UserEntity user = UserEntity.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .build();

            assertEquals("John Doe", user.getFullName());
        }

        @Test
        @DisplayName("Should return first name only when last name is null")
        void shouldReturnFirstNameOnlyWhenLastNameIsNull() {
            UserEntity user = UserEntity.builder()
                    .firstName("John")
                    .build();

            assertEquals("John", user.getFullName());
        }

        @Test
        @DisplayName("Should return last name only when first name is null")
        void shouldReturnLastNameOnlyWhenFirstNameIsNull() {
            UserEntity user = UserEntity.builder()
                    .lastName("Doe")
                    .build();

            assertEquals("Doe", user.getFullName());
        }

        @Test
        @DisplayName("Should return Unknown when both names are null")
        void shouldReturnUnknownWhenBothNamesAreNull() {
            UserEntity user = UserEntity.builder()
                    .email("test@example.com")
                    .build();

            assertEquals("Unknown", user.getFullName());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            UserEntity user = new UserEntity();
            user.setId(testId);
            user.setEmail("test@example.com");
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setPhoneNumber("+1234567890");
            user.setAddress("123 Street");
            user.setCity("City");
            user.setKeycloakUserId("keycloak-id");
            user.setProfilePictureUrl("http://pic.jpg");
            user.setBio("Bio text");
            user.setRole("ADMIN");
            user.setIsActive(false);
            user.setCreatedAt(testDateTime);
            user.setUpdatedAt(testDateTime);

            assertEquals(testId, user.getId());
            assertEquals("test@example.com", user.getEmail());
            assertEquals("John", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("+1234567890", user.getPhoneNumber());
            assertEquals("123 Street", user.getAddress());
            assertEquals("City", user.getCity());
            assertEquals("keycloak-id", user.getKeycloakUserId());
            assertEquals("http://pic.jpg", user.getProfilePictureUrl());
            assertEquals("Bio text", user.getBio());
            assertEquals("ADMIN", user.getRole());
            assertFalse(user.getIsActive());
            assertEquals(testDateTime, user.getCreatedAt());
            assertEquals(testDateTime, user.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            UserEntity user = new UserEntity();

            assertNotNull(user);
            assertNull(user.getId());
            assertNull(user.getEmail());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            UserEntity user = new UserEntity(
                    testId,
                    "test@example.com",
                    "John",
                    "Doe",
                    "+1234567890",
                    "123 Street",
                    "City",
                    "keycloak-id",
                    "http://pic.jpg",
                    "Bio",
                    "USER",
                    true,
                    testDateTime,
                    testDateTime
            );

            assertEquals(testId, user.getId());
            assertEquals("test@example.com", user.getEmail());
            assertEquals("John", user.getFirstName());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            UserEntity user = UserEntity.builder()
                    .id(testId)
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            String toString = user.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("test@example.com"));
            assertTrue(toString.contains("John"));
        }
    }

    @Nested
    @DisplayName("Entity State Tests")
    class EntityStateTests {

        @Test
        @DisplayName("Should update user state")
        void shouldUpdateUserState() {
            UserEntity user = UserEntity.builder()
                    .email("original@example.com")
                    .firstName("Original")
                    .build();

            user.setEmail("updated@example.com");
            user.setFirstName("Updated");

            assertEquals("updated@example.com", user.getEmail());
            assertEquals("Updated", user.getFirstName());
        }

        @Test
        @DisplayName("Should handle null keycloakUserId")
        void shouldHandleNullKeycloakUserId() {
            UserEntity user = UserEntity.builder()
                    .email("test@example.com")
                    .build();

            assertNull(user.getKeycloakUserId());
        }
    }
}

