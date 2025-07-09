package space.banterbox.feature.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.projection.UserPreviewProjection;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsUsersByUsername(String username);

    Optional<User> findUserByUsername(String username);

    @Query("""
        SELECT
            u.id as id,
            u.username as username,
            u.displayName as displayName,
            u.profilePictureId as profilePictureId,
            EXISTS (
                   SELECT 1 FROM UsersFollower uf
                       WHERE uf.following.id = u.id
                           AND uf.follower.id = :currentUserId
            ) as followedByCurrentUser
        FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<UserPreviewProjection> searchByUsernameOrDisplayName(@Param("query") String query,
                                                              @Param("currentUserId") UUID currentUserId,
                                                              Pageable pageable);
}
