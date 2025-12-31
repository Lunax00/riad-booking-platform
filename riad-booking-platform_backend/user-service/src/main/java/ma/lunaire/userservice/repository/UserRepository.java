package ma.lunaire.userservice.repository;

import ma.lunaire.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByKeycloakUserId(String keycloakUserId);


    boolean existsByEmail(String email);

    boolean existsByKeycloakUserId(String keycloakUserId);
}