package ma.lunaire.userservice.dto;

import ma.lunaire.userservice.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserResponse Tests")
class UserResponseTest {

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
        @DisplayName("Should build UserResponse with all fields")
        void shouldBuildWithAllFields() {
            UserResponse response = UserResponse.builder()
                    .id(testId)
                    .email("test@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .fullName("John Doe")
                    .phoneNumber("+1234567890")
                    .address("123 Test Street")
                    .city("Test City")
                    .profilePictureUrl("http://example.com/pic.jpg")
                    .bio("Test bio")
                    .role("USER")
                    .isActive(true)
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .build();

            assertNotNull(response);
            assertEquals(testId, response.getId());
            assertEquals("test@example.com", response.getEmail());
            assertEquals("John", response.getFirstName());
            assertEquals("Doe", response.getLastName());
            assertEquals("John Doe", response.getFullName());
            assertEquals("+1234567890", response.getPhoneNumber());
            assertEquals("123 Test Street", response.getAddress());
            assertEquals("Test City", response.getCity());
            assertEquals("http://example.com/pic.jpg", response.getProfilePictureUrl());
            assertEquals("Test bio", response.getBio());
            assertEquals("USER", response.getRole());
            assertTrue(response.getIsActive());
            assertEquals(testDateTime, response.getCreatedAt());
            assertEquals(testDateTime, response.getUpdatedAt());
        }

        @Test
        @DisplayName("Should build UserResponse with minimal fields")
        void shouldBuildWithMinimalFields() {
            UserResponse response = UserResponse.builder()
                    .id(testId)
                    .email("test@example.com")
                    .build();

            assertNotNull(response);
            assertEquals(testId, response.getId());
            assertEquals("test@example.com", response.getEmail());
            assertNull(response.getFirstName());
        }
    }

    @Nested
    @DisplayName("FromEntity Tests")
    class FromEntityTests {

        @Test
        @DisplayName("Should convert entity to response with all fields")
        void shouldConvertEntityToResponseWithAllFields() {
            UserEntity entity = UserEntity.builder()
                    .id(testId)
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
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .build();

            UserResponse response = UserResponse.fromEntity(entity);

            assertNotNull(response);
            assertEquals(testId, response.getId());
            assertEquals("test@example.com", response.getEmail());
            assertEquals("John", response.getFirstName());
            assertEquals("Doe", response.getLastName());
            assertEquals("John Doe", response.getFullName());
            assertEquals("+1234567890", response.getPhoneNumber());
            assertEquals("123 Test Street", response.getAddress());
            assertEquals("Test City", response.getCity());
            assertEquals("http://example.com/pic.jpg", response.getProfilePictureUrl());
            assertEquals("Test bio", response.getBio());
            assertEquals("USER", response.getRole());
            assertTrue(response.getIsActive());
            assertEquals(testDateTime, response.getCreatedAt());
            assertEquals(testDateTime, response.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle entity with only first name for full name")
        void shouldHandleEntityWithOnlyFirstName() {
            UserEntity entity = UserEntity.builder()
                    .id(testId)
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            UserResponse response = UserResponse.fromEntity(entity);

            assertEquals("John", response.getFullName());
        }

        @Test
        @DisplayName("Should handle entity with only last name for full name")
        void shouldHandleEntityWithOnlyLastName() {
            UserEntity entity = UserEntity.builder()
                    .id(testId)
                    .email("test@example.com")
                    .lastName("Doe")
                    .build();

            UserResponse response = UserResponse.fromEntity(entity);

            assertEquals("Doe", response.getFullName());
        }

        @Test
        @DisplayName("Should handle entity with no name for full name")
        void shouldHandleEntityWithNoName() {
            UserEntity entity = UserEntity.builder()
                    .id(testId)
                    .email("test@example.com")
                    .build();

            UserResponse response = UserResponse.fromEntity(entity);

            assertEquals("Unknown", response.getFullName());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            UserResponse response = new UserResponse();
            response.setId(testId);
            response.setEmail("test@example.com");
            response.setFirstName("John");
            response.setLastName("Doe");
            response.setFullName("John Doe");
            response.setPhoneNumber("+1234567890");
            response.setAddress("123 Street");
            response.setCity("City");
            response.setProfilePictureUrl("http://pic.jpg");
            response.setBio("Bio text");
            response.setRole("ADMIN");
            response.setIsActive(false);
            response.setCreatedAt(testDateTime);
            response.setUpdatedAt(testDateTime);

            assertEquals(testId, response.getId());
            assertEquals("test@example.com", response.getEmail());
            assertEquals("John", response.getFirstName());
            assertEquals("Doe", response.getLastName());
            assertEquals("John Doe", response.getFullName());
            assertEquals("+1234567890", response.getPhoneNumber());
            assertEquals("123 Street", response.getAddress());
            assertEquals("City", response.getCity());
            assertEquals("http://pic.jpg", response.getProfilePictureUrl());
            assertEquals("Bio text", response.getBio());
            assertEquals("ADMIN", response.getRole());
            assertFalse(response.getIsActive());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same values")
        void shouldBeEqualWhenSameValues() {
            UserResponse response1 = UserResponse.builder()
                    .id(testId)
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            UserResponse response2 = UserResponse.builder()
                    .id(testId)
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            assertEquals(response1, response2);
            assertEquals(response1.hashCode(), response2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different IDs")
        void shouldNotBeEqualWhenDifferentIds() {
            UserResponse response1 = UserResponse.builder()
                    .id(UUID.randomUUID())
                    .email("test@example.com")
                    .build();

            UserResponse response2 = UserResponse.builder()
                    .id(UUID.randomUUID())
                    .email("test@example.com")
                    .build();

            assertNotEquals(response1, response2);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            UserResponse response = new UserResponse();

            assertNotNull(response);
            assertNull(response.getId());
            assertNull(response.getEmail());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            UserResponse response = new UserResponse(
                    testId,
                    "test@example.com",
                    "John",
                    "Doe",
                    "John Doe",
                    "+1234567890",
                    "123 Street",
                    "City",
                    "http://pic.jpg",
                    "Bio",
                    "USER",
                    true,
                    testDateTime,
                    testDateTime
            );

            assertEquals(testId, response.getId());
            assertEquals("test@example.com", response.getEmail());
            assertEquals("John", response.getFirstName());
            assertEquals("Doe", response.getLastName());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            UserResponse response = UserResponse.builder()
                    .id(testId)
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            String toString = response.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("test@example.com"));
            assertTrue(toString.contains("John"));
        }
    }
}

