package space.banterbox.feature.post.feed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import space.banterbox.feature.post.dto.PostDto;

import java.util.UUID;

@Repository
public interface PostFeedRepository {

    Page<PostDto> getGlobalFeed(Pageable pageable);

    Page<PostDto> getPrivateFeed(UUID userId, Pageable pageable);
}
