package space.banterbox.feature.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import space.banterbox.feature.notification.dto.PagedNotificationDto;
import space.banterbox.feature.notification.mapper.NotificationMapper;
import space.banterbox.feature.notification.projection.NotificationProjection;
import space.banterbox.feature.notification.repository.NotificationRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public PagedNotificationDto getNotifications(UUID viewerUserId, int page, int size) {
        Page<NotificationProjection> pagedData = notificationRepository.findAllByRecipientId(viewerUserId, PageRequest.of(page, size));

        var notifications = pagedData.getContent().stream().map(notificationMapper::toDto)
                .toList();

        return new PagedNotificationDto(
                notifications,
                pagedData.getNumber(),
                pagedData.getSize(),
                pagedData.getTotalElements(),
                pagedData.getTotalPages(),
                pagedData.isLast()
        );
    }

    public long getUnreadCount(UUID viewerUserId) {
        return notificationRepository.countByActorIdEqualsAndRead(viewerUserId, false);
    }

    @Transactional
    public List<UUID> markAsRead(List<UUID> notificationIds) {
        notificationRepository.markAsRead(notificationIds);
        return notificationIds;
    }
}
