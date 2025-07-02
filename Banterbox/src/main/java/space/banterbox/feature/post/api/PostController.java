package space.banterbox.feature.post.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import space.banterbox.core.response.PagedResponse;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.dto.request.CreatePostRequest;
import space.banterbox.feature.post.feed.service.PostFeedService;
import space.banterbox.feature.post.service.PostService;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostFeedService postFeedService;

    @PostMapping
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CreatePostRequest request,
            UriComponentsBuilder uriBuilder) {
        var savedPost = postService.createPost(userId, request.getContent());
        var location = uriBuilder.path("/posts/{id}").buildAndExpand(savedPost.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/feed/global")
    public ResponseEntity<PagedResponse<PostDto>> getGlobalFeed(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Page<PostDto> pagedData = postFeedService.getGlobalFeed(page, size);
        return ResponseEntity.ok(getPagedResponse(pagedData));
    }

    @GetMapping("/feed/private")
    public ResponseEntity<PagedResponse<PostDto>> getPrivateFeed(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Page<PostDto> pagedData     = postFeedService.getPrivateFeed(userId, page, size);
        return ResponseEntity.ok(getPagedResponse(pagedData));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @AuthenticationPrincipal UUID userId,
            @PathVariable("postId") UUID postId
    ) {
        postService.likePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal UUID userId,
            @PathVariable("postId") UUID postId
    ) {
        postService.unlikePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Map<String, Long>> getPostLikesCount(@PathVariable("postId") UUID postId) {
        long count = postService.countLikes(postId);
        return ResponseEntity.ok(Map.of("likes", count));
    }

    private <T> PagedResponse<T> getPagedResponse(Page<T> pagedData) {
        return new PagedResponse<>(
                pagedData.getContent(),
                pagedData.getNumber(),
                pagedData.getSize(),
                pagedData.getTotalElements(),
                pagedData.getTotalPages(),
                pagedData.isLast()
        );
    }

}
