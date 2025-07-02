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
import space.banterbox.feature.post.service.PostService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

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
        Page<PostDto> pagedData = postService.getGlobalFeed(page, size);
        return ResponseEntity.ok(getPagedResponse(pagedData));
    }

    @GetMapping("/feed/private")
    public ResponseEntity<PagedResponse<PostDto>> getPrivateFeed(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Page<PostDto> pagedData = postService.getPrivateFeed(userId, page, size);
        return ResponseEntity.ok(getPagedResponse(pagedData));
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
