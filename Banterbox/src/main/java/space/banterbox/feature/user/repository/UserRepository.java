package space.banterbox.feature.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsUsersByUsername(String username);

    Optional<User> findUserByUsername(String username);

    @Query(value = """
        SELECT new space.banterbox.feature.user.dto.response.UserPreviewDto(
            u.id,
            u.username,
            u.displayName,
            u.profilePictureId
        )
        FROM User u
    """, countQuery = "SELECT COUNT(*) FROM User")
    Page<UserPreviewDto> getAllUsersWithPreview(Pageable pageable);
}
