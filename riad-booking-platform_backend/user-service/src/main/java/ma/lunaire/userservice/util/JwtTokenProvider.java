package ma.lunaire.userservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * JwtTokenProvider - Utility class for extracting information from JWT tokens
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final Supplier<Authentication> authenticationSupplier;

    /**
     * Default constructor using SecurityContextHolder
     */
    public JwtTokenProvider() {
        this(() -> SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Constructor for testing with custom authentication supplier
     */
    public JwtTokenProvider(Supplier<Authentication> authenticationSupplier) {
        this.authenticationSupplier = authenticationSupplier;
    }

    /**
     * Get the current JWT from the security context
     */
    public Optional<Jwt> getCurrentJwt() {
        Authentication authentication = authenticationSupplier.get();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found in security context");
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof Jwt) {
            return Optional.of((Jwt) authentication.getPrincipal());
        }

        log.debug("Principal is not a JWT: {}", authentication.getPrincipal().getClass().getName());
        return Optional.empty();
    }

    /**
     * Get the subject (user ID) from the current JWT
     */
    public Optional<String> getCurrentUserId() {
        return getCurrentJwt().map(Jwt::getSubject);
    }

    /**
     * Get the email claim from the current JWT
     */
    public Optional<String> getCurrentUserEmail() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("email"));
    }

    /**
     * Get the preferred username from the current JWT
     */
    public Optional<String> getCurrentUsername() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("preferred_username"));
    }

    /**
     * Get the first name from the current JWT
     */
    public Optional<String> getCurrentUserFirstName() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("given_name"));
    }

    /**
     * Get the last name from the current JWT
     */
    public Optional<String> getCurrentUserLastName() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("family_name"));
    }

    /**
     * Get the full name from the current JWT
     */
    public Optional<String> getCurrentUserFullName() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("name"));
    }

    /**
     * Get roles from the current JWT (from realm_access.roles)
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserRoles() {
        return getCurrentJwt()
                .map(jwt -> {
                    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
                    if (realmAccess != null && realmAccess.containsKey("roles")) {
                        Object roles = realmAccess.get("roles");
                        if (roles instanceof List) {
                            return (List<String>) roles;
                        }
                    }
                    return Collections.<String>emptyList();
                })
                .orElse(Collections.emptyList());
    }

    /**
     * Check if the current user has a specific role
     */
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    /**
     * Check if the current user is an admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("admin");
    }

    /**
     * Get a specific claim from the current JWT
     */
    public Optional<Object> getClaim(String claimName) {
        return getCurrentJwt().map(jwt -> jwt.getClaim(claimName));
    }

    /**
     * Get a specific claim as String from the current JWT
     */
    public Optional<String> getClaimAsString(String claimName) {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString(claimName));
    }

    /**
     * Get the token issuer
     */
    public Optional<String> getIssuer() {
        return getCurrentJwt().map(jwt -> jwt.getIssuer().toString());
    }

    /**
     * Get the token expiration time in seconds since epoch
     */
    public Optional<Long> getExpirationTime() {
        return getCurrentJwt()
                .map(Jwt::getExpiresAt)
                .map(Instant::getEpochSecond);
    }

    /**
     * Check if the current token is expired
     */
    public boolean isTokenExpired() {
        return getCurrentJwt()
                .map(jwt -> {
                    if (jwt.getExpiresAt() == null) {
                        return false;
                    }
                    return jwt.getExpiresAt().isBefore(Instant.now());
                })
                .orElse(true);
    }

    /**
     * Check if there is a valid authenticated user
     */
    public boolean isAuthenticated() {
        return getCurrentJwt().isPresent() && !isTokenExpired();
    }
}
