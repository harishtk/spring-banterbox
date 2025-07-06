package space.banterbox.feature.user.dto;

import space.banterbox.feature.user.dto.response.UserProfileDto;

import java.util.List;

public record PagedUserProfileDto(
        List<UserProfileDto> users,
        int page,
        int size,
        long totalUsers,
        int totalPages,
        boolean isLastPage
) {
}