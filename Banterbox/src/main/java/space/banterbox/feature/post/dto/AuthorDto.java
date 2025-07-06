package space.banterbox.feature.post.dto;

import java.util.UUID;

public record AuthorDto(
    UUID id,
    String username,
    String displayName,
    String profilePictureId
) {}
