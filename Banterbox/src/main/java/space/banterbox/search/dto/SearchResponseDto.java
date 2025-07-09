package space.banterbox.search.dto;

import space.banterbox.feature.post.dto.PostSummaryDto;
import space.banterbox.feature.user.dto.response.UserPreviewDto;

import java.util.List;

public record SearchResponseDto(
        List<UserPreviewDto> users,
        List<PostSummaryDto> posts,
        int usersCount,
        int postsCount
) { }
