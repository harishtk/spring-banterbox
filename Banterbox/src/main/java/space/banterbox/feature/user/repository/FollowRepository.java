package space.banterbox.feature.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.model.UsersFollower;
import space.banterbox.feature.user.model.UsersFollowerId;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<UsersFollower, UsersFollowerId> {

    List<UsersFollower> findAllByFollowingId(UUID userId);
    List<UsersFollower> findAllByFollowerId(UUID userId);

    @Query(value = """
        SELECT u
        FROM UsersFollower uf
        JOIN User u ON uf.id.followerId = u.id
        WHERE uf.id.followingId = :userId
    """, countQuery = "SELECT COUNT(uf) FROM UsersFollower uf WHERE uf.id.followingId = :userId")
    Page<User> findFollowersOf(@Param("userId") UUID userId, Pageable pageable);

    @Query(value = """
        SELECT u
        FROM UsersFollower uf
        JOIN User u ON uf.id.followingId = u.id
        WHERE uf.id.followerId = :userId
    """, countQuery = "SELECT COUNT(uf) FROM UsersFollower uf WHERE uf.id.followerId = :userId")
    Page<User> findFollowingOf(@Param("userId") UUID userId, Pageable pageable);

    Long countUsersFollowersByFollowingId(UUID followingId);
    Long countUsersFollowingByFollowerId(UUID followerId);
}
