package space.banterbox.feature.user.dto.response;

import java.util.UUID;

public record UserPreviewDto(
        UUID id,
        String username,
        String displayName,
        String profilePictureId,
        Boolean isFollowing
) {}