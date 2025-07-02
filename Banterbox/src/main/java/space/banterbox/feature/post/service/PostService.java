package space.banterbox.feature.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.mapper.PostMapper;
import space.banterbox.feature.post.model.Post;
import space.banterbox.feature.post.repository.PostRepository;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.repository.FollowRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FollowRepository followRepository;

    public PostDto createPost(UUID userId, String content) {
        Post post = new Post();
        User author = new User();
        author.setId(userId);

        post.setAuthor(author);
        post.setContent(content);

        var savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

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

    public Page<PostDto> getPostsByAuthor(UUID userId, int page, int size) {
        return postRepository.findPostsByFollowedAuthors(List.of(userId), PageRequest.of(page, size));
    }

}
