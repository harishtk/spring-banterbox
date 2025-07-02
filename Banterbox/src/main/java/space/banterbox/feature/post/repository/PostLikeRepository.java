package space.banterbox.feature.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.post.model.PostLike;
import space.banterbox.feature.post.model.PostLikeId;

import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    long countLikesByPostId(UUID postId);

    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END FROM PostLike pl WHERE pl.id = :id")
    boolean existsLike(@Param("id") PostLikeId id);

}
