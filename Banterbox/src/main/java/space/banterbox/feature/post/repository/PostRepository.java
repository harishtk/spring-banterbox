package space.banterbox.feature.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.post.model.Post;
import space.banterbox.feature.post.projection.PostWithLikes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = """
        SELECT
            p.id as id,
            p.content as content,
            p.createdAt as createdAt,
            p.updatedAt as updatedAt,
            p.author.id as authorId,
            p.author as author,
            COUNT(DISTINCT pl.id) as likesCount,
            EXISTS(
                SELECT 1
                FROM PostLike ul
                WHERE ul.post = p
                AND ul.user.id = :currentUserId
            ) as likedByCurrentUser
        FROM Post p
        LEFT JOIN PostLike pl ON pl.post.id = p.id
        GROUP BY p.id, p.content, p.createdAt, p.updatedAt, p.author
        ORDER BY p.createdAt DESC
    """, countQuery = "SELECT COUNT(p) FROM Post p")
    Page<PostWithLikes> findAllPosts(@Param("currentUserId") UUID currentUserId, Pageable pageable);

    @Query(value = """
        SELECT
            p.id as id,
            p.content as content,
            p.createdAt as createdAt,
            p.updatedAt as updatedAt,
            p.author.id as authorId,
            p.author as author,
            COUNT(DISTINCT pl.id) as likesCount,
            EXISTS(
                SELECT 1
                FROM PostLike ul
                WHERE ul.post = p
                AND ul.user.id = :currentUserId
            ) as likedByCurrentUser
        FROM Post p
        LEFT JOIN PostLike pl ON pl.post.id = p.id
        WHERE p.author.id IN :followedUserIds
        GROUP BY p.id, p.content, p.createdAt, p.updatedAt, p.author
        ORDER BY p.createdAt DESC
    """, countQuery = "SELECT COUNT(p) FROM Post p WHERE p.author.id IN :ids")
    Page<PostWithLikes> findPostsByFollowedAuthors(
            @Param("followedUserIds") List<UUID> followedUserIds,
            @Param("currentUserId") UUID currentUserId,
            Pageable pageable);

    @Query("""
        SELECT
            p.id as id,
            p.content as content,
            p.createdAt as createdAt,
            p.updatedAt as updatedAt,
            p.author.id as authorId,
            p.author as author,
            COUNT(DISTINCT pl.id) as likesCount,
            EXISTS(
                SELECT 1
                FROM PostLike ul
                WHERE ul.post = p
                AND ul.user.id = :currentUserId
            ) as likedByCurrentUser
        FROM Post p
        LEFT JOIN PostLike pl ON pl.post.id = p.id
        WHERE p.id = :postId
        GROUP BY p.id, p.content, p.createdAt, p.updatedAt, p.author
    """)
    Optional<PostWithLikes> findPostWithLikes(@Param("postId") UUID postId, @Param("currentUserId") UUID currentUserId);
}
