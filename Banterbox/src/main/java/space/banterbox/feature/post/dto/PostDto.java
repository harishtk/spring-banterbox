package space.banterbox.feature.post.dto;

import java.time.Instant;
import java.util.UUID;

public record PostDto(
        UUID id,
        UUID authorId,
        String content,
        Instant createdAt,
        Instant updatedAt,
        long likesCount,
        boolean likedByCurrentUser
) {}

