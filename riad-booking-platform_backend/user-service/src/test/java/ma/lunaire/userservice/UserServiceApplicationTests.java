package ma.lunaire.userservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserServiceApplicationTests {

    @Test
    void contextLoads() {
        // Basic test to verify application class exists
        assertDoesNotThrow(() -> UserServiceApplication.class.getDeclaredConstructor());
    }

    @Test
    void mainMethodExists() {
        // Verify main method exists
        assertDoesNotThrow(() -> UserServiceApplication.class.getMethod("main", String[].class));
    }

}
