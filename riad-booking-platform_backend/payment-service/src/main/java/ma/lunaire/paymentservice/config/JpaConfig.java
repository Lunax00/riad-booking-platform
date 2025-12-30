package ma.lunaire.paymentservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration for auditing (CreationTimestamp, UpdateTimestamp).
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}

