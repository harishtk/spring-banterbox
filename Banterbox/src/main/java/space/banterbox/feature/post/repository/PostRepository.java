package space.banterbox.feature.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = """
        SELECT new space.banterbox.feature.post.dto.PostDto(
            p.id,
            a.id,
            a.username,
            p.content,
            p.createdAt,
            COUNT(pl.id)
        )
        FROM Post p
        JOIN p.author a
        LEFT JOIN PostLike pl ON pl.post.id = p.id
        GROUP BY p.id, a.id
        ORDER BY p.createdAt DESC
    """, countQuery = "SELECT COUNT(p) FROM Post p")
    Page<PostDto> findAllPosts(Pageable pageable);

    @Query(value = """
        SELECT new space.banterbox.feature.post.dto.PostDto(
            p.id,
            a.id,
            a.username,
            p.content,
            p.createdAt,
            COUNT(pl.id)
        )
        FROM Post p
        JOIN p.author a
        LEFT JOIN PostLike pl ON pl.post.id = p.id
        WHERE a.id IN :followedUserIds
        GROUP BY p.id, a.id
        ORDER BY p.createdAt DESC
    """, countQuery = "SELECT COUNT(p) FROM Post p WHERE p.author.id IN :ids")
    Page<PostDto> findPostsByFollowedAuthors(@Param("followedUserIds") List<UUID> followedUserIds, Pageable pageable);

    @Query(value = """
        SELECT new space.banterbox.feature.post.dto.PostDto(
            p.id,
            a.id,
            a.username,
            p.content,
            p.createdAt,
            COUNT(pl.id)
        )
        FROM Post p
        JOIN p.author a
        LEFT JOIN PostLike pl ON pl.post.id = p.id
        WHERE p.id = :postId
        GROUP BY p.id, a.id
        ORDER BY p.createdAt DESC
    """, countQuery = "SELECT COUNT(p) FROM Post p")
    Optional<PostDto> findPostBy(@Param("postId") UUID postId);
}
