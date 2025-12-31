package ma.lunaire.userservice.service;

import ma.lunaire.userservice.dto.UserRequest;
import ma.lunaire.userservice.dto.UserResponse;
import ma.lunaire.userservice.entity.UserEntity;
import ma.lunaire.userservice.exception.UserNotFoundException;
import ma.lunaire.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserService - Logique métier pour les utilisateurs
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Créer un nouvel utilisateur
     */
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Création d'un nouvel utilisateur avec email: {}", userRequest.getEmail());

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            log.warn("Tentative de création avec email existant: {}", userRequest.getEmail());
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        UserEntity user = UserEntity.builder()
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phoneNumber(userRequest.getPhoneNumber())
                .address(userRequest.getAddress())
                .city(userRequest.getCity())
                .profilePictureUrl(userRequest.getProfilePictureUrl())
                .bio(userRequest.getBio())
                .role("USER")
                .isActive(true)
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès. ID: {}", savedUser.getId());

        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Récupérer le profil de l'utilisateur courant (connecté)
     */
    public UserResponse getCurrentUserProfile() {
        log.info("Récupération du profil de l'utilisateur courant");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Aucun utilisateur authentifié trouvé");
            throw new UserNotFoundException("Aucun utilisateur authentifié");
        }

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String keycloakUserId = jwt.getSubject();
            String email = jwt.getClaimAsString("email");

            log.debug("Recherche d'utilisateur avec Keycloak ID: {}", keycloakUserId);

            UserEntity user = userRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> {
                        log.warn("Utilisateur non trouvé pour Keycloak ID: {}", keycloakUserId);
                        return new UserNotFoundException("Utilisateur non trouvé");
                    });

            return UserResponse.fromEntity(user);
        }

        throw new UserNotFoundException("Format d'authentification non reconnu");
    }


    public UserResponse getUserById(UUID id) {
        log.info("Récupération de l'utilisateur avec ID: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé avec ID: {}", id);
                    return new UserNotFoundException("Utilisateur non trouvé");
                });

        return UserResponse.fromEntity(user);
    }


    public UserResponse getUserByEmail(String email) {
        log.info("Récupération de l'utilisateur avec email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé avec email: {}", email);
                    return new UserNotFoundException("Utilisateur non trouvé");
                });

        return UserResponse.fromEntity(user);
    }


    public List<UserResponse> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");

        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }


    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        log.info("Mise à jour de l'utilisateur avec ID: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé avec ID: {}", id);
                    return new UserNotFoundException("Utilisateur non trouvé");
                });

        if (userRequest.getFirstName() != null) {
            user.setFirstName(userRequest.getFirstName());
        }
        if (userRequest.getLastName() != null) {
            user.setLastName(userRequest.getLastName());
        }
        if (userRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(userRequest.getPhoneNumber());
        }
        if (userRequest.getAddress() != null) {
            user.setAddress(userRequest.getAddress());
        }
        if (userRequest.getCity() != null) {
            user.setCity(userRequest.getCity());
        }
        if (userRequest.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(userRequest.getProfilePictureUrl());
        }
        if (userRequest.getBio() != null) {
            user.setBio(userRequest.getBio());
        }

        UserEntity updatedUser = userRepository.save(user);
        log.info("Utilisateur modifié avec succès. ID: {}", id);

        return UserResponse.fromEntity(updatedUser);
    }

    public void deleteUser(UUID id) {
        log.info("Suppression de l'utilisateur avec ID: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("Utilisateur non trouvé avec ID: {}", id);
            throw new UserNotFoundException("Utilisateur non trouvé");
        }

        userRepository.deleteById(id);
        log.info("Utilisateur supprimé avec succès. ID: {}", id);
    }


    public UserEntity linkUserToKeycloak(String email, String keycloakUserId) {
        log.info("Liaison utilisateur {} avec Keycloak ID: {}", email, keycloakUserId);

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> UserEntity.builder()
                        .email(email)
                        .role("USER")
                        .isActive(true)
                        .build());

        user.setKeycloakUserId(keycloakUserId);
        UserEntity savedUser = userRepository.save(user);
        log.info("Utilisateur lié avec succès. ID: {}", savedUser.getId());

        return savedUser;
    }
}