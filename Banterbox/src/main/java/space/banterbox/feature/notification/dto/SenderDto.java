package space.banterbox.feature.notification.dto;

import java.util.UUID;

public record SenderDto(
        UUID id,
        String username,
        String displayName,
        String profilePictureId
) {}
