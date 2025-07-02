package space.banterbox.feature.post.feed.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import space.banterbox.feature.post.dto.PostDto;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PostFeedRepositoryImpl implements PostFeedRepository {

    private final EntityManager em;

    @Override
    public Page<PostDto> getGlobalFeed(Pageable pageable) {
        var query = em.createQuery("""
            SELECT new space.banterbox.feature.post.dto.PostDto(
                p.id, a.id, a.username, p.content, p.createdAt
            )
            FROM Post p
            JOIN p.author a
            ORDER BY p.createdAt DESC
        """, PostDto.class);

        List<PostDto> content = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = em.createQuery("SELECT COUNT(p) FROM Post p", Long.class)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostDto> getPrivateFeed(UUID userId, Pageable pageable) {
        var query = em.createQuery("""
            SELECT new space.banterbox.feature.post.dto.PostDto(
                p.id, a.id, a.username, p.content, p.createdAt
            )
            FROM Post p
            JOIN p.author a
            WHERE a.id IN (
                SELECT f.following.id
                FROM UsersFollower f
                WHERE f.follower.id = :userId
            )
            ORDER BY p.createdAt DESC
        """, PostDto.class);

        query.setParameter("userId", userId);

        List<PostDto> content = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = em.createQuery("""
            SELECT COUNT(p)
            FROM Post p
            WHERE p.author.id IN (
                SELECT f.following.id
                FROM UsersFollower f
                WHERE f.follower.id = :userId
            )
        """, Long.class).setParameter("userId", userId).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
