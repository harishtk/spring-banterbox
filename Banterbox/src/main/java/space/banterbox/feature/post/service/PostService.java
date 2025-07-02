package space.banterbox.feature.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.mapper.PostMapper;
import space.banterbox.feature.post.model.Post;
import space.banterbox.feature.post.model.PostLike;
import space.banterbox.feature.post.model.PostLikeId;
import space.banterbox.feature.post.repository.PostLikeRepository;
import space.banterbox.feature.post.repository.PostRepository;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.repository.FollowRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PostMapper postMapper;
    private final PostLikeRepository postLikeRepository;

    public PostDto createPost(UUID userId, String content) {
        Post post = new Post();
        User author = new User();
        author.setId(userId);

        post.setAuthor(author);
        post.setContent(content);

        var savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    public Page<PostDto> getPostsByAuthor(UUID userId, int page, int size) {
        return postRepository.findPostsByFollowedAuthors(List.of(userId), PageRequest.of(page, size));
    }

    /* Feeds */
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
    /* END - Feeds */

    /* Post likes */
    public void likePost(UUID postId, UUID userId) {
        PostLikeId id = new PostLikeId(postId, userId);
        if (postLikeRepository.existsLike(id)) {
            throw new IllegalStateException("Already liked");
        }

        Post post = postRepository.getReferenceById(postId);
        User user = new User();
        user.setId(userId);

        PostLike like = new PostLike();
        like.setId(id);
        like.setUser(user);
        like.setPost(post);

        postLikeRepository.save(like);
    }

    public void unlikePost(UUID postId, UUID userId) {
        PostLikeId id = new PostLikeId(postId, userId);
        if (!postLikeRepository.existsLike(id)) {
            throw new IllegalStateException("Not liked");
        }

        postLikeRepository.deleteById(id);
    }

    public long countLikes(UUID postId) {
        return postLikeRepository.countLikesByPostId(postId);
    }

    /* END - Post likes */}
