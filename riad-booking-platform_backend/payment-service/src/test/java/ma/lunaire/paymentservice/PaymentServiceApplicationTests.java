package ma.lunaire.paymentservice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic application test - does not require full Spring context.
 */
class PaymentServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertThat(PaymentServiceApplication.class).isNotNull();
    }

    @Test
    void mainMethodExists() throws NoSuchMethodException {
        assertThat(PaymentServiceApplication.class.getMethod("main", String[].class)).isNotNull();
    }
}

