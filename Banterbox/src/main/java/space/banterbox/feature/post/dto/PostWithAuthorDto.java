package space.banterbox.feature.post.dto;

import java.util.List;

public record PostWithAuthorDto(
    PostDto post,
    List<AuthorDto> users
) {}

