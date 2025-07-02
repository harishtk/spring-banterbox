package space.banterbox.feature.post.feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.repository.PostRepository;
import space.banterbox.feature.user.repository.FollowRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostFeedService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    public Page<PostDto> getGlobalFeed(int page, int size) {
        return postRepository.findAllPosts(PageRequest.of(page, size));
    }

    public Page<PostDto> getPrivateFeed(UUID userId, int page, int size) {
        List<UUID> followingIds = followRepository.findAllByFollowerId(userId)
                .stream()
                .map(f -> f.getId().getFollowingId())
                .toList();
        return postRepository.findPostsByFollowedAuthors(followingIds, PageRequest.of(page, size));
    }
}
