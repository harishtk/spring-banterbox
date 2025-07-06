package space.banterbox.feature.user.dto;

import space.banterbox.feature.user.dto.response.UserPreviewDto;

import java.util.List;

public record PagedUserPreviewDto(
        List<UserPreviewDto> users,
        int page,
        int size,
        long totalUsers,
        int totalPages,
        boolean isLastPage
) {
}
