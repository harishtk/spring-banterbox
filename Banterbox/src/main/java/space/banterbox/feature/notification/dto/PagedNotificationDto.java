package space.banterbox.feature.notification.dto;

import java.util.List;

public record PagedNotificationDto(
        List<NotificationDto> notifications,
        int page,
        int pageSize,
        long totalNotifications,
        int totalPages,
        boolean isLastPage
) {}
