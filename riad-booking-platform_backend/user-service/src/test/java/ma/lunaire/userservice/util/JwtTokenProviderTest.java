package ma.lunaire.userservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // Default setup with null authentication
        jwtTokenProvider = new JwtTokenProvider(() -> null);
    }

    private void setupAuthenticatedJwt() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        jwtTokenProvider = new JwtTokenProvider(() -> authentication);
    }

    private void setupUnauthenticated() {
        jwtTokenProvider = new JwtTokenProvider(() -> null);
    }

    private void setupNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        jwtTokenProvider = new JwtTokenProvider(() -> authentication);
    }

    private void setupNonJwtPrincipal() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("not-a-jwt");
        jwtTokenProvider = new JwtTokenProvider(() -> authentication);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with default constructor")
        void shouldCreateWithDefaultConstructor() {
            JwtTokenProvider provider = new JwtTokenProvider();
            assertNotNull(provider);
        }

        @Test
        @DisplayName("Should create with custom supplier")
        void shouldCreateWithCustomSupplier() {
            JwtTokenProvider provider = new JwtTokenProvider(() -> authentication);
            assertNotNull(provider);
        }
    }

    @Nested
    @DisplayName("GetCurrentJwt Tests")
    class GetCurrentJwtTests {

        @Test
        @DisplayName("Should return JWT when authenticated")
        void shouldReturnJwtWhenAuthenticated() {
            setupAuthenticatedJwt();

            Optional<Jwt> result = jwtTokenProvider.getCurrentJwt();

            assertTrue(result.isPresent());
            assertEquals(jwt, result.get());
        }

        @Test
        @DisplayName("Should return empty when no authentication")
        void shouldReturnEmptyWhenNoAuthentication() {
            setupUnauthenticated();

            Optional<Jwt> result = jwtTokenProvider.getCurrentJwt();

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should return empty when not authenticated")
        void shouldReturnEmptyWhenNotAuthenticated() {
            setupNotAuthenticated();

            Optional<Jwt> result = jwtTokenProvider.getCurrentJwt();

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should return empty when principal is not JWT")
        void shouldReturnEmptyWhenPrincipalIsNotJwt() {
            setupNonJwtPrincipal();

            Optional<Jwt> result = jwtTokenProvider.getCurrentJwt();

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("GetCurrentUserId Tests")
    class GetCurrentUserIdTests {

        @Test
        @DisplayName("Should return user ID from JWT subject")
        void shouldReturnUserIdFromJwtSubject() {
            setupAuthenticatedJwt();
            when(jwt.getSubject()).thenReturn("user-123");

            Optional<String> result = jwtTokenProvider.getCurrentUserId();

            assertTrue(result.isPresent());
            assertEquals("user-123", result.get());
        }

        @Test
        @DisplayName("Should return empty when not authenticated")
        void shouldReturnEmptyWhenNotAuthenticated() {
            setupUnauthenticated();

            Optional<String> result = jwtTokenProvider.getCurrentUserId();

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("GetCurrentUserEmail Tests")
    class GetCurrentUserEmailTests {

        @Test
        @DisplayName("Should return email from JWT claim")
        void shouldReturnEmailFromJwtClaim() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsString("email")).thenReturn("user@example.com");

            Optional<String> result = jwtTokenProvider.getCurrentUserEmail();

            assertTrue(result.isPresent());
            assertEquals("user@example.com", result.get());
        }

        @Test
        @DisplayName("Should return empty when email claim is missing")
        void shouldReturnEmptyWhenEmailClaimIsMissing() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsString("email")).thenReturn(null);

            Optional<String> result = jwtTokenProvider.getCurrentUserEmail();

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("GetCurrentUsername Tests")
    class GetCurrentUsernameTests {

        @Test
        @DisplayName("Should return preferred username from JWT claim")
        void shouldReturnPreferredUsernameFromJwtClaim() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsString("preferred_username")).thenReturn("johndoe");

            Optional<String> result = jwtTokenProvider.getCurrentUsername();

            assertTrue(result.isPresent());
            assertEquals("johndoe", result.get());
        }
    }

    @Nested
    @DisplayName("GetCurrentUserFirstName Tests")
    class GetCurrentUserFirstNameTests {

        @Test
        @DisplayName("Should return given name from JWT claim")
        void shouldReturnGivenNameFromJwtClaim() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsString("given_name")).thenReturn("John");

            Optional<String> result = jwtTokenProvider.getCurrentUserFirstName();

            assertTrue(result.isPresent());
            assertEquals("John", result.get());
        }
    }

    @Nested
    @DisplayName("GetCurrentUserLastName Tests")
    class GetCurrentUserLastNameTests {

        @Test
        @DisplayName("Should return family name from JWT claim")
        void shouldReturnFamilyNameFromJwtClaim() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsString("family_name")).thenReturn("Doe");

            Optional<String> result = jwtTokenProvider.getCurrentUserLastName();

            assertTrue(result.isPresent());
            assertEquals("Doe", result.get());
        }
    }

    @Nested
    @DisplayName("GetCurrentUserFullName Tests")
    class GetCurrentUserFullNameTests {

        @Test
        @DisplayName("Should return full name from JWT claim")
        void shouldReturnFullNameFromJwtClaim() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsString("name")).thenReturn("John Doe");

            Optional<String> result = jwtTokenProvider.getCurrentUserFullName();

            assertTrue(result.isPresent());
            assertEquals("John Doe", result.get());
        }
    }

    @Nested
    @DisplayName("GetCurrentUserRoles Tests")
    class GetCurrentUserRolesTests {

        @Test
        @DisplayName("Should return roles from realm_access claim")
        void shouldReturnRolesFromRealmAccessClaim() {
            setupAuthenticatedJwt();
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", Arrays.asList("USER", "ADMIN"));
            when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

            List<String> result = jwtTokenProvider.getCurrentUserRoles();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains("USER"));
            assertTrue(result.contains("ADMIN"));
        }

        @Test
        @DisplayName("Should return empty list when realm_access is null")
        void shouldReturnEmptyListWhenRealmAccessIsNull() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsMap("realm_access")).thenReturn(null);

            List<String> result = jwtTokenProvider.getCurrentUserRoles();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list when roles not present")
        void shouldReturnEmptyListWhenRolesNotPresent() {
            setupAuthenticatedJwt();
            Map<String, Object> realmAccess = new HashMap<>();
            when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

            List<String> result = jwtTokenProvider.getCurrentUserRoles();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list when not authenticated")
        void shouldReturnEmptyListWhenNotAuthenticated() {
            setupUnauthenticated();

            List<String> result = jwtTokenProvider.getCurrentUserRoles();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("HasRole Tests")
    class HasRoleTests {

        @Test
        @DisplayName("Should return true when user has role")
        void shouldReturnTrueWhenUserHasRole() {
            setupAuthenticatedJwt();
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", Arrays.asList("USER", "ADMIN"));
            when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

            assertTrue(jwtTokenProvider.hasRole("ADMIN"));
        }

        @Test
        @DisplayName("Should return false when user does not have role")
        void shouldReturnFalseWhenUserDoesNotHaveRole() {
            setupAuthenticatedJwt();
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", Arrays.asList("USER"));
            when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

            assertFalse(jwtTokenProvider.hasRole("ADMIN"));
        }
    }

    @Nested
    @DisplayName("IsAdmin Tests")
    class IsAdminTests {

        @Test
        @DisplayName("Should return true when user has ADMIN role")
        void shouldReturnTrueWhenUserHasAdminRole() {
            setupAuthenticatedJwt();
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", Arrays.asList("USER", "ADMIN"));
            when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

            assertTrue(jwtTokenProvider.isAdmin());
        }

        @Test
        @DisplayName("Should return true when user has admin role (lowercase)")
        void shouldReturnTrueWhenUserHasAdminRoleLowercase() {
            setupAuthenticatedJwt();
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", Arrays.asList("user", "admin"));
            when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

            assertTrue(jwtTokenProvider.isAdmin());
        }

        @Test
        @DisplayName("Should return false when user is not admin")
        void shouldReturnFalseWhenUserIsNotAdmin() {
            setupAuthenticatedJwt();
            Map<String, Object> realmAccess = new HashMap<>();
            realmAccess.put("roles", Arrays.asList("USER"));
            when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

            assertFalse(jwtTokenProvider.isAdmin());
        }
    }

    @Nested
    @DisplayName("GetClaim Tests")
    class GetClaimTests {

        @Test
        @DisplayName("Should return claim value")
        void shouldReturnClaimValue() {
            setupAuthenticatedJwt();
            when(jwt.getClaim("custom_claim")).thenReturn("custom_value");

            Optional<Object> result = jwtTokenProvider.getClaim("custom_claim");

            assertTrue(result.isPresent());
            assertEquals("custom_value", result.get());
        }

        @Test
        @DisplayName("Should return empty when claim not present")
        void shouldReturnEmptyWhenClaimNotPresent() {
            setupAuthenticatedJwt();
            when(jwt.getClaim("missing_claim")).thenReturn(null);

            Optional<Object> result = jwtTokenProvider.getClaim("missing_claim");

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("GetClaimAsString Tests")
    class GetClaimAsStringTests {

        @Test
        @DisplayName("Should return claim as string")
        void shouldReturnClaimAsString() {
            setupAuthenticatedJwt();
            when(jwt.getClaimAsString("string_claim")).thenReturn("string_value");

            Optional<String> result = jwtTokenProvider.getClaimAsString("string_claim");

            assertTrue(result.isPresent());
            assertEquals("string_value", result.get());
        }
    }

    @Nested
    @DisplayName("GetIssuer Tests")
    class GetIssuerTests {

        @Test
        @DisplayName("Should return issuer from JWT")
        void shouldReturnIssuerFromJwt() throws Exception {
            setupAuthenticatedJwt();
            java.net.URL issuerUrl = new java.net.URL("http://localhost:8080/realms/test");
            when(jwt.getIssuer()).thenReturn(issuerUrl);

            Optional<String> result = jwtTokenProvider.getIssuer();

            assertTrue(result.isPresent());
            assertTrue(result.get().contains("localhost:8080"));
        }

        @Test
        @DisplayName("Should return issuer as string")
        void shouldReturnIssuerAsString() throws Exception {
            setupAuthenticatedJwt();
            java.net.URL issuerUrl = new java.net.URL("http://keycloak.local/realms/riad");
            when(jwt.getIssuer()).thenReturn(issuerUrl);

            Optional<String> result = jwtTokenProvider.getIssuer();

            assertTrue(result.isPresent());
            assertEquals("http://keycloak.local/realms/riad", result.get());
        }
    }

    @Nested
    @DisplayName("GetExpirationTime Tests")
    class GetExpirationTimeTests {

        @Test
        @DisplayName("Should return expiration time in epoch seconds")
        void shouldReturnExpirationTimeInEpochSeconds() {
            setupAuthenticatedJwt();
            Instant expiresAt = Instant.now().plusSeconds(3600);
            when(jwt.getExpiresAt()).thenReturn(expiresAt);

            Optional<Long> result = jwtTokenProvider.getExpirationTime();

            assertTrue(result.isPresent());
            assertEquals(expiresAt.getEpochSecond(), result.get());
        }

        @Test
        @DisplayName("Should return empty when no expiration")
        void shouldReturnEmptyWhenNoExpiration() {
            setupAuthenticatedJwt();
            when(jwt.getExpiresAt()).thenReturn(null);

            Optional<Long> result = jwtTokenProvider.getExpirationTime();

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("IsTokenExpired Tests")
    class IsTokenExpiredTests {

        @Test
        @DisplayName("Should return false when token is not expired")
        void shouldReturnFalseWhenTokenIsNotExpired() {
            setupAuthenticatedJwt();
            Instant futureTime = Instant.now().plusSeconds(3600);
            when(jwt.getExpiresAt()).thenReturn(futureTime);

            assertFalse(jwtTokenProvider.isTokenExpired());
        }

        @Test
        @DisplayName("Should return true when token is expired")
        void shouldReturnTrueWhenTokenIsExpired() {
            setupAuthenticatedJwt();
            Instant pastTime = Instant.now().minusSeconds(3600);
            when(jwt.getExpiresAt()).thenReturn(pastTime);

            assertTrue(jwtTokenProvider.isTokenExpired());
        }

        @Test
        @DisplayName("Should return false when no expiration time")
        void shouldReturnFalseWhenNoExpirationTime() {
            setupAuthenticatedJwt();
            when(jwt.getExpiresAt()).thenReturn(null);

            assertFalse(jwtTokenProvider.isTokenExpired());
        }

        @Test
        @DisplayName("Should return true when not authenticated")
        void shouldReturnTrueWhenNotAuthenticated() {
            setupUnauthenticated();

            assertTrue(jwtTokenProvider.isTokenExpired());
        }
    }

    @Nested
    @DisplayName("IsAuthenticated Tests")
    class IsAuthenticatedTests {

        @Test
        @DisplayName("Should return true when authenticated with valid token")
        void shouldReturnTrueWhenAuthenticatedWithValidToken() {
            setupAuthenticatedJwt();
            Instant futureTime = Instant.now().plusSeconds(3600);
            when(jwt.getExpiresAt()).thenReturn(futureTime);

            assertTrue(jwtTokenProvider.isAuthenticated());
        }

        @Test
        @DisplayName("Should return false when not authenticated")
        void shouldReturnFalseWhenNotAuthenticated() {
            setupUnauthenticated();

            assertFalse(jwtTokenProvider.isAuthenticated());
        }

        @Test
        @DisplayName("Should return false when token is expired")
        void shouldReturnFalseWhenTokenIsExpired() {
            setupAuthenticatedJwt();
            Instant pastTime = Instant.now().minusSeconds(3600);
            when(jwt.getExpiresAt()).thenReturn(pastTime);

            assertFalse(jwtTokenProvider.isAuthenticated());
        }
    }
}
