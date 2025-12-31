package ma.lunaire.userservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Nested
    @DisplayName("UserNotFoundException Handler Tests")
    class UserNotFoundExceptionHandlerTests {

        @Test
        @DisplayName("Should handle UserNotFoundException and return 404")
        void shouldHandleUserNotFoundException() {
            UserNotFoundException exception = new UserNotFoundException("User not found");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception);

            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().getStatus());
            assertEquals("User not found", response.getBody().getMessage());
            assertEquals("NOT_FOUND", response.getBody().getError());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        @DisplayName("Should include user ID in error message")
        void shouldIncludeUserIdInErrorMessage() {
            String message = "User not found with id: 123";
            UserNotFoundException exception = new UserNotFoundException(message);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception);

            assertTrue(response.getBody().getMessage().contains("123"));
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException Handler Tests")
    class IllegalArgumentExceptionHandlerTests {

        @Test
        @DisplayName("Should handle IllegalArgumentException and return 400")
        void shouldHandleIllegalArgumentException() {
            IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
            assertEquals("Invalid argument", response.getBody().getMessage());
            assertEquals("BAD_REQUEST", response.getBody().getError());
        }

        @Test
        @DisplayName("Should handle email already exists error")
        void shouldHandleEmailAlreadyExistsError() {
            String message = "Un utilisateur avec cet email existe déjà";
            IllegalArgumentException exception = new IllegalArgumentException(message);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

            assertEquals(message, response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException Handler Tests")
    class ValidationExceptionHandlerTests {

        @Test
        @DisplayName("Should handle validation errors and return 400")
        void shouldHandleValidationErrors() {
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError fieldError = new FieldError(
                    "userRequest",
                    "email",
                    "Email ne peut pas être vide"
            );

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationException(exception);

            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().get("status"));
            assertEquals("Validation failed", response.getBody().get("message"));
            assertNotNull(response.getBody().get("timestamp"));
            assertNotNull(response.getBody().get("errors"));

            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertEquals("Email ne peut pas être vide", errors.get("email"));
        }

        @Test
        @DisplayName("Should handle multiple validation errors")
        void shouldHandleMultipleValidationErrors() {
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError emailError = new FieldError("userRequest", "email", "Email invalide");
            FieldError firstNameError = new FieldError("userRequest", "firstName", "Prénom trop long");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(emailError, firstNameError));

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationException(exception);

            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertEquals(2, errors.size());
            assertEquals("Email invalide", errors.get("email"));
            assertEquals("Prénom trop long", errors.get("firstName"));
        }
    }

    @Nested
    @DisplayName("General Exception Handler Tests")
    class GeneralExceptionHandlerTests {

        @Test
        @DisplayName("Should handle general Exception and return 500")
        void shouldHandleGeneralException() {
            Exception exception = new Exception("Unexpected error");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(exception);

            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(500, response.getBody().getStatus());
            assertEquals("Une erreur inattendue s'est produite", response.getBody().getMessage());
            assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
        }

        @Test
        @DisplayName("Should handle NullPointerException")
        void shouldHandleNullPointerException() {
            NullPointerException exception = new NullPointerException("Null value");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(exception);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(500, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should handle RuntimeException")
        void shouldHandleRuntimeException() {
            RuntimeException exception = new RuntimeException("Runtime error");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(exception);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Response Format Tests")
    class ResponseFormatTests {

        @Test
        @DisplayName("ErrorResponse should have timestamp")
        void errorResponseShouldHaveTimestamp() {
            UserNotFoundException exception = new UserNotFoundException("Test");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception);

            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        @DisplayName("ErrorResponse should have correct HTTP status code")
        void errorResponseShouldHaveCorrectHttpStatusCode() {
            UserNotFoundException notFoundException = new UserNotFoundException("Not found");
            IllegalArgumentException badRequestException = new IllegalArgumentException("Bad request");
            Exception serverException = new Exception("Server error");

            ResponseEntity<ErrorResponse> notFoundResponse = exceptionHandler.handleUserNotFoundException(notFoundException);
            ResponseEntity<ErrorResponse> badRequestResponse = exceptionHandler.handleIllegalArgumentException(badRequestException);
            ResponseEntity<ErrorResponse> serverResponse = exceptionHandler.handleGeneralException(serverException);

            assertEquals(404, notFoundResponse.getBody().getStatus());
            assertEquals(400, badRequestResponse.getBody().getStatus());
            assertEquals(500, serverResponse.getBody().getStatus());
        }
    }
}

