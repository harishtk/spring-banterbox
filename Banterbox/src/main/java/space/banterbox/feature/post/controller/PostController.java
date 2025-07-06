package space.banterbox.feature.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import space.banterbox.core.dto.ErrorDto;
import space.banterbox.core.response.StandardResponse;
import space.banterbox.feature.post.dto.PostFeedDto;
import space.banterbox.feature.post.dto.PostWithAuthorDto;
import space.banterbox.feature.post.dto.request.CreatePostRequest;
import space.banterbox.feature.post.exception.PostLikeUnlikeException;
import space.banterbox.feature.post.exception.PostNotFoundException;
import space.banterbox.feature.post.service.PostService;

import java.util.UUID;

@Tag(name = "Posts", description = "Endpoints for managing posts and interactions")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post with the given content. Returns the created post with author information.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Post successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    @PostMapping
    public ResponseEntity<StandardResponse<PostWithAuthorDto>> createPost(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CreatePostRequest request,
            UriComponentsBuilder uriBuilder) {
        var savedPost = postService.createPost(userId, request.getContent());
        var location = uriBuilder.path("/posts/{id}").buildAndExpand(savedPost.post().id()).toUri();
        return ResponseEntity.created(location).body(StandardResponse.success(HttpStatus.CREATED, savedPost));
    }

    @Operation(
            summary = "Get post by ID",
            description = "Retrieves a post by its ID with author information and interaction status for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post found and returned"),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @GetMapping("/{postId}")
    public ResponseEntity<StandardResponse<PostWithAuthorDto>> getPostById(
            @AuthenticationPrincipal UUID viewerUserId,
            @PathVariable("postId") UUID postId) {
        return ResponseEntity.ok(StandardResponse.success(postService.getPostById(postId, viewerUserId)));
    }

    @Operation(
            summary = "Get global feed",
            description = "Retrieves paginated list of all posts sorted by creation date. Includes interaction status for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
            }
    )
    @GetMapping("/feed/global")
    public ResponseEntity<StandardResponse<PostFeedDto>> getGlobalFeed(
            @AuthenticationPrincipal UUID viewerUserId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(StandardResponse.success(postService.getGlobalFeed(viewerUserId, page, size)));
    }

    @Operation(
            summary = "Get private feed",
            description = "Retrieves paginated list of posts from users that the current user follows, sorted by creation date",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
            }
    )

    @GetMapping("/feed/private")
    public ResponseEntity<StandardResponse<PostFeedDto>> getPrivateFeed(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(StandardResponse.success(postService.getPrivateFeed(userId, page, size)));
    }


    @Operation(
            summary = "Get posts by author",
            description = "Retrieves paginated list of posts from a specific author. Includes interaction status for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Author not found")
            }
    )
    @GetMapping
    public ResponseEntity<StandardResponse<PostFeedDto>> getPostsByQuery(
            @AuthenticationPrincipal UUID viewerUserId,
            @RequestParam(name = "authorId") UUID authorId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                StandardResponse.success(postService.getPostsByAuthor(authorId, viewerUserId, page, size)));    
    }

    @Operation(
            summary = "Like a post",
            description = "Adds like to the specified post. A user can only like a post once",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post liked successfully"),
                    @ApiResponse(responseCode = "404", description = "Post not found"),
                    @ApiResponse(responseCode = "409", description = "Post already liked by user")
            }
    )
    @PostMapping("/{postId}/like")
    public ResponseEntity<StandardResponse<PostWithAuthorDto>> likePost(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable("postId") UUID postId
    ) {
        var postDto = postService.likePost(postId, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(postDto));
    }

    @Operation(
            summary = "Unlike a post",
            description = "Removes user's like from the specified post",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post unliked successfully"),
                    @ApiResponse(responseCode = "404", description = "Post not found"),
                    @ApiResponse(responseCode = "409", description = "Post was not liked by user")
            }
    )
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<StandardResponse<PostWithAuthorDto>> unlikePost(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable("postId") UUID postId
    ) {
        var postDto = postService.unlikePost(postId, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(postDto));
    }

    @Operation(
            summary = "Get likes count",
            description = "Returns the total number of likes for the specified post",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Like count retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<StandardResponse<ErrorDto>> handlePostNotFoundException(PostNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                StandardResponse.of(HttpStatus.NOT_FOUND.value(), "Not found", new ErrorDto(exception.getMessage()))
        );
    }

    @ExceptionHandler(PostLikeUnlikeException.class)
    public ResponseEntity<StandardResponse<ErrorDto>> handlePostLikeUnlikeException(PostLikeUnlikeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                StandardResponse.of(HttpStatus.CONFLICT.value(), "Conflict", new ErrorDto(exception.getMessage()))
        );
    }
}
