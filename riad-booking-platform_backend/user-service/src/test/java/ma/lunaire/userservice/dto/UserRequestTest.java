package ma.lunaire.userservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserRequest Tests")
class UserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build UserRequest with all fields")
        void shouldBuildWithAllFields() {
            UserRequest request = UserRequest.builder()
                    .email("test@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .phoneNumber("+1234567890")
                    .address("123 Test Street")
                    .city("Test City")
                    .profilePictureUrl("http://example.com/pic.jpg")
                    .bio("Test bio")
                    .build();

            assertNotNull(request);
            assertEquals("test@example.com", request.getEmail());
            assertEquals("John", request.getFirstName());
            assertEquals("Doe", request.getLastName());
            assertEquals("+1234567890", request.getPhoneNumber());
            assertEquals("123 Test Street", request.getAddress());
            assertEquals("Test City", request.getCity());
            assertEquals("http://example.com/pic.jpg", request.getProfilePictureUrl());
            assertEquals("Test bio", request.getBio());
        }

        @Test
        @DisplayName("Should build UserRequest with minimal fields")
        void shouldBuildWithMinimalFields() {
            UserRequest request = UserRequest.builder()
                    .email("test@example.com")
                    .build();

            assertNotNull(request);
            assertEquals("test@example.com", request.getEmail());
            assertNull(request.getFirstName());
            assertNull(request.getLastName());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should pass validation with valid email")
        void shouldPassValidationWithValidEmail() {
            UserRequest request = UserRequest.builder()
                    .email("valid@example.com")
                    .build();

            Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation with blank email")
        void shouldFailValidationWithBlankEmail() {
            UserRequest request = UserRequest.builder()
                    .email("")
                    .build();

            Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation with invalid email format")
        void shouldFailValidationWithInvalidEmailFormat() {
            UserRequest request = UserRequest.builder()
                    .email("invalid-email")
                    .build();

            Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation with invalid phone number")
        void shouldFailValidationWithInvalidPhoneNumber() {
            UserRequest request = UserRequest.builder()
                    .email("test@example.com")
                    .phoneNumber("123")
                    .build();

            Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
        }

        @Test
        @DisplayName("Should pass validation with valid phone number")
        void shouldPassValidationWithValidPhoneNumber() {
            UserRequest request = UserRequest.builder()
                    .email("test@example.com")
                    .phoneNumber("+1234567890")
                    .build();

            Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation with too long firstName")
        void shouldFailValidationWithTooLongFirstName() {
            UserRequest request = UserRequest.builder()
                    .email("test@example.com")
                    .firstName("A".repeat(101))
                    .build();

            Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
        }

        @Test
        @DisplayName("Should fail validation with too long bio")
        void shouldFailValidationWithTooLongBio() {
            UserRequest request = UserRequest.builder()
                    .email("test@example.com")
                    .bio("A".repeat(5001))
                    .build();

            Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("bio")));
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get email")
        void shouldSetAndGetEmail() {
            UserRequest request = new UserRequest();
            request.setEmail("test@example.com");

            assertEquals("test@example.com", request.getEmail());
        }

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            UserRequest request = new UserRequest();
            request.setEmail("test@example.com");
            request.setFirstName("John");
            request.setLastName("Doe");
            request.setPhoneNumber("+1234567890");
            request.setAddress("123 Street");
            request.setCity("City");
            request.setProfilePictureUrl("http://pic.jpg");
            request.setBio("Bio text");

            assertEquals("test@example.com", request.getEmail());
            assertEquals("John", request.getFirstName());
            assertEquals("Doe", request.getLastName());
            assertEquals("+1234567890", request.getPhoneNumber());
            assertEquals("123 Street", request.getAddress());
            assertEquals("City", request.getCity());
            assertEquals("http://pic.jpg", request.getProfilePictureUrl());
            assertEquals("Bio text", request.getBio());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same values")
        void shouldBeEqualWhenSameValues() {
            UserRequest request1 = UserRequest.builder()
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            UserRequest request2 = UserRequest.builder()
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            assertEquals(request1, request2);
            assertEquals(request1.hashCode(), request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different values")
        void shouldNotBeEqualWhenDifferentValues() {
            UserRequest request1 = UserRequest.builder()
                    .email("test1@example.com")
                    .build();

            UserRequest request2 = UserRequest.builder()
                    .email("test2@example.com")
                    .build();

            assertNotEquals(request1, request2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            UserRequest request = UserRequest.builder()
                    .email("test@example.com")
                    .firstName("John")
                    .build();

            String toString = request.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("test@example.com"));
            assertTrue(toString.contains("John"));
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            UserRequest request = new UserRequest();

            assertNotNull(request);
            assertNull(request.getEmail());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            UserRequest request = new UserRequest(
                    "test@example.com",
                    "John",
                    "Doe",
                    "+1234567890",
                    "123 Street",
                    "City",
                    "http://pic.jpg",
                    "Bio"
            );

            assertEquals("test@example.com", request.getEmail());
            assertEquals("John", request.getFirstName());
            assertEquals("Doe", request.getLastName());
        }
    }
}

