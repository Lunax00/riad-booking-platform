package ma.lunaire.userservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserNotFoundException Tests")
class UserNotFoundExceptionTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create exception with message only")
        void shouldCreateExceptionWithMessageOnly() {
            String message = "User not found";
            UserNotFoundException exception = new UserNotFoundException(message);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            String message = "User not found";
            Throwable cause = new RuntimeException("Original error");
            UserNotFoundException exception = new UserNotFoundException(message, cause);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }
    }

    @Nested
    @DisplayName("Exception Behavior Tests")
    class ExceptionBehaviorTests {

        @Test
        @DisplayName("Should be a RuntimeException")
        void shouldBeRuntimeException() {
            UserNotFoundException exception = new UserNotFoundException("test");

            assertTrue(exception instanceof RuntimeException);
        }

        @Test
        @DisplayName("Should be throwable")
        void shouldBeThrowable() {
            assertThrows(UserNotFoundException.class, () -> {
                throw new UserNotFoundException("User not found");
            });
        }

        @Test
        @DisplayName("Should preserve message when thrown")
        void shouldPreserveMessageWhenThrown() {
            String expectedMessage = "User with ID 123 not found";

            try {
                throw new UserNotFoundException(expectedMessage);
            } catch (UserNotFoundException e) {
                assertEquals(expectedMessage, e.getMessage());
            }
        }

        @Test
        @DisplayName("Should preserve cause when thrown")
        void shouldPreserveCauseWhenThrown() {
            RuntimeException cause = new RuntimeException("Database error");
            String message = "User not found";

            try {
                throw new UserNotFoundException(message, cause);
            } catch (UserNotFoundException e) {
                assertEquals(message, e.getMessage());
                assertEquals(cause, e.getCause());
                assertEquals("Database error", e.getCause().getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Use Case Tests")
    class UseCaseTests {

        @Test
        @DisplayName("Should work in typical find by ID scenario")
        void shouldWorkInFindByIdScenario() {
            String userId = "123e4567-e89b-12d3-a456-426614174000";
            String message = "User not found with id: " + userId;

            UserNotFoundException exception = new UserNotFoundException(message);

            assertTrue(exception.getMessage().contains(userId));
        }

        @Test
        @DisplayName("Should work in typical find by email scenario")
        void shouldWorkInFindByEmailScenario() {
            String email = "notfound@example.com";
            String message = "User not found with email: " + email;

            UserNotFoundException exception = new UserNotFoundException(message);

            assertTrue(exception.getMessage().contains(email));
        }
    }
}

