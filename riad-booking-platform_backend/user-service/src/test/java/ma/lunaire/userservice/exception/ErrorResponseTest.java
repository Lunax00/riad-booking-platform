package ma.lunaire.userservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build ErrorResponse with all fields")
        void shouldBuildWithAllFields() {
            ErrorResponse error = ErrorResponse.builder()
                    .status(404)
                    .message("User not found")
                    .error("NOT_FOUND")
                    .timestamp(testDateTime)
                    .build();

            assertNotNull(error);
            assertEquals(404, error.getStatus());
            assertEquals("User not found", error.getMessage());
            assertEquals("NOT_FOUND", error.getError());
            assertEquals(testDateTime, error.getTimestamp());
        }

        @Test
        @DisplayName("Should build ErrorResponse with partial fields")
        void shouldBuildWithPartialFields() {
            ErrorResponse error = ErrorResponse.builder()
                    .status(500)
                    .message("Internal error")
                    .build();

            assertNotNull(error);
            assertEquals(500, error.getStatus());
            assertEquals("Internal error", error.getMessage());
            assertNull(error.getError());
            assertNull(error.getTimestamp());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            ErrorResponse error = new ErrorResponse();

            assertNotNull(error);
            assertEquals(0, error.getStatus());
            assertNull(error.getMessage());
            assertNull(error.getError());
            assertNull(error.getTimestamp());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            ErrorResponse error = new ErrorResponse(
                    400,
                    "Bad request",
                    "BAD_REQUEST",
                    testDateTime
            );

            assertEquals(400, error.getStatus());
            assertEquals("Bad request", error.getMessage());
            assertEquals("BAD_REQUEST", error.getError());
            assertEquals(testDateTime, error.getTimestamp());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            ErrorResponse error = new ErrorResponse();
            error.setStatus(403);
            error.setMessage("Forbidden");
            error.setError("FORBIDDEN");
            error.setTimestamp(testDateTime);

            assertEquals(403, error.getStatus());
            assertEquals("Forbidden", error.getMessage());
            assertEquals("FORBIDDEN", error.getError());
            assertEquals(testDateTime, error.getTimestamp());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same values")
        void shouldBeEqualWhenSameValues() {
            ErrorResponse error1 = ErrorResponse.builder()
                    .status(404)
                    .message("Not found")
                    .error("NOT_FOUND")
                    .timestamp(testDateTime)
                    .build();

            ErrorResponse error2 = ErrorResponse.builder()
                    .status(404)
                    .message("Not found")
                    .error("NOT_FOUND")
                    .timestamp(testDateTime)
                    .build();

            assertEquals(error1, error2);
            assertEquals(error1.hashCode(), error2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different status")
        void shouldNotBeEqualWhenDifferentStatus() {
            ErrorResponse error1 = ErrorResponse.builder()
                    .status(404)
                    .build();

            ErrorResponse error2 = ErrorResponse.builder()
                    .status(500)
                    .build();

            assertNotEquals(error1, error2);
        }
    }

    @Nested
    @DisplayName("HTTP Status Use Cases")
    class HttpStatusUseCases {

        @Test
        @DisplayName("Should represent 404 Not Found")
        void shouldRepresent404NotFound() {
            ErrorResponse error = ErrorResponse.builder()
                    .status(404)
                    .message("User not found")
                    .error("NOT_FOUND")
                    .timestamp(LocalDateTime.now())
                    .build();

            assertEquals(404, error.getStatus());
            assertEquals("NOT_FOUND", error.getError());
        }

        @Test
        @DisplayName("Should represent 400 Bad Request")
        void shouldRepresent400BadRequest() {
            ErrorResponse error = ErrorResponse.builder()
                    .status(400)
                    .message("Invalid input")
                    .error("BAD_REQUEST")
                    .timestamp(LocalDateTime.now())
                    .build();

            assertEquals(400, error.getStatus());
            assertEquals("BAD_REQUEST", error.getError());
        }

        @Test
        @DisplayName("Should represent 500 Internal Server Error")
        void shouldRepresent500InternalServerError() {
            ErrorResponse error = ErrorResponse.builder()
                    .status(500)
                    .message("An unexpected error occurred")
                    .error("INTERNAL_SERVER_ERROR")
                    .timestamp(LocalDateTime.now())
                    .build();

            assertEquals(500, error.getStatus());
            assertEquals("INTERNAL_SERVER_ERROR", error.getError());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            ErrorResponse error = ErrorResponse.builder()
                    .status(404)
                    .message("Not found")
                    .error("NOT_FOUND")
                    .build();

            String toString = error.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("404"));
            assertTrue(toString.contains("Not found"));
            assertTrue(toString.contains("NOT_FOUND"));
        }
    }
}

