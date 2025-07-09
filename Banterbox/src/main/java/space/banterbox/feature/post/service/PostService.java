package space.banterbox.feature.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import space.banterbox.feature.notification.NotificationManager;
import space.banterbox.feature.post.dto.AuthorDto;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.dto.PostFeedDto;
import space.banterbox.feature.post.dto.PostWithAuthorDto;
import space.banterbox.feature.post.exception.PostLikeUnlikeException;
import space.banterbox.feature.post.exception.PostNotFoundException;
import space.banterbox.feature.post.mapper.AuthorMapper;
import space.banterbox.feature.post.mapper.PostMapper;
import space.banterbox.feature.post.model.Post;
import space.banterbox.feature.post.model.PostLike;
import space.banterbox.feature.post.model.PostLikeId;
import space.banterbox.feature.post.projection.PostWithLikes;
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
    private final AuthorMapper authorMapper;
    private final NotificationManager notificationManager;

    public PostWithAuthorDto createPost(UUID userId, String content) {
        Post post = new Post();
        User author = new User();
        author.setId(userId);

        post.setAuthor(author);
        post.setContent(content);

        var savedPost = postRepository.save(post);
        return getPostById(savedPost.getId(), userId);
    }

    public PostWithAuthorDto getPostById(UUID postId, UUID currentUserId) {
        var postWithLikes = postRepository.findPostWithLikes(postId, currentUserId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        PostDto post = postMapper.toDto(postWithLikes);
        List<AuthorDto> users = List.of(authorMapper.toDto(postWithLikes.getAuthor()));
        return new PostWithAuthorDto(post, users);
    }

    public PostFeedDto getPostsByAuthor(UUID authorId, UUID viewerUserId, int page, int size) {
        Page<PostWithLikes> pagedData = postRepository.findPostsByFollowedAuthors(List.of(authorId), viewerUserId, PageRequest.of(page, size));
        return getPostFeedDto(pagedData);
    }

    /* Feeds */
    public PostFeedDto getGlobalFeed(UUID userId, int page, int size) {
        Page<PostWithLikes> pagedData = postRepository.findAllPosts(userId, PageRequest.of(page, size));
        return getPostFeedDto(pagedData);
    }

    private PostFeedDto getPostFeedDto(Page<PostWithLikes> pagedData) {
        List<PostDto> posts = pagedData.getContent().stream().map(postMapper::toDto).toList();
        List<AuthorDto> users = pagedData.getContent().stream().map(PostWithLikes::getAuthor).distinct().map(authorMapper::toDto).toList();

        return new PostFeedDto(
                posts,
                users,
                PostFeedDto.PageMetadata.from(pagedData)
        );
    }

    public PostFeedDto getPrivateFeed(UUID userId, int page, int size) {
        List<UUID> followingIds = followRepository.findAllByFollowerId(userId)
                .stream()
                .map(f -> f.getId().getFollowingId())
                .toList();

        Page<PostWithLikes> pagedData = postRepository.findPostsByFollowedAuthors(followingIds, userId, PageRequest.of(page, size));
        return getPostFeedDto(pagedData);
    }
    /* END - Feeds */

    /* Post likes */
    public PostWithAuthorDto likePost(UUID postId, UUID userId) {
        PostLikeId id = new PostLikeId(postId, userId);
        if (postLikeRepository.existsLike(id)) {
            throw new PostLikeUnlikeException("Already liked");
        }

        Post post = postRepository.getReferenceById(postId);
        User user = new User();
        user.setId(userId);

        PostLike like = new PostLike();
        like.setId(id);
        like.setUser(user);
        like.setPost(post);

        postLikeRepository.save(like);

        notificationManager.createPostLikedNotification(userId, postId);

        // Returning an updated post
        return getPostById(postId, userId);
    }

    public PostWithAuthorDto unlikePost(UUID postId, UUID userId) {
        PostLikeId id = new PostLikeId(postId, userId);
        if (!postLikeRepository.existsLike(id)) {
            throw new PostLikeUnlikeException("Not liked");
        }

        postLikeRepository.deleteById(id);
        return getPostById(postId, userId);
    }
    /* END - Post likes */}
