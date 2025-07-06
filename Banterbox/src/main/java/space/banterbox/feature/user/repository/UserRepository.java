package space.banterbox.feature.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import space.banterbox.feature.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsUsersByUsername(String username);

    Optional<User> findUserByUsername(String username);
}
