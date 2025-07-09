package space.banterbox.feature.post.dto;

import space.banterbox.feature.user.dto.response.UserPreviewDto;

import java.time.Instant;
import java.util.UUID;

public record PostSummaryDto(
        UUID id,
        UUID authorId,
        String content,
        Instant createdAt,
        long likesCount,
        boolean likedByCurrentUser,
        UserPreviewDto author
) {}