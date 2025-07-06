package space.banterbox.feature.user.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserProfileDto(
     UUID id,
     String username,
     String displayName,
     String bio,
     String profilePictureId,
     Instant createdAt,
     long followersCount,
     long followingCount,
     boolean isFollowing,
     boolean isSelf
) {}

