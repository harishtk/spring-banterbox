package space.banterbox.feature.users.service;

import org.springframework.data.jpa.repository.JpaRepository;
import space.banterbox.feature.users.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUsersByUsername(String username);

    Optional<User> findUserByUsername(String username);
}
