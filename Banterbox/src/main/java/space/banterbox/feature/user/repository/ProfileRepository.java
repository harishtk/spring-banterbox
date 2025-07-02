package space.banterbox.feature.user.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.model.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends CrudRepository<Profile, UUID> {

    @Query("""
        SELECT new space.banterbox.feature.user.dto.response.UserProfileDto(
            u.id,
            u.username,
            p.displayName,
            p.bio,
            p.profilePictureId,
            p.createdAt,
            (SELECT COUNT(f) FROM UsersFollower f WHERE f.follower.id = u.id),
            (SELECT COUNT(f) FROM UsersFollower f WHERE f.following.id = u.id),
            (SELECT COUNT(pst) FROM Post pst WHERE pst.author.id = u.id)
        )
        FROM User u
        JOIN Profile p ON u.id = p.id
        WHERE u.id = :userId
    """)
    Optional<UserProfileDto> findProfileWithStat(@Param("userId") UUID userId);
}