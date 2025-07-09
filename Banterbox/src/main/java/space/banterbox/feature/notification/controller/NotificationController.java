package space.banterbox.feature.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import space.banterbox.core.response.StandardResponse;
import space.banterbox.feature.notification.dto.PagedNotificationDto;
import space.banterbox.feature.notification.dto.request.NotificationMarkRequestDto;
import space.banterbox.feature.notification.dto.response.UnreadCountDto;
import space.banterbox.feature.notification.service.NotificationService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<StandardResponse<PagedNotificationDto>> getNotifications(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(StandardResponse.success(
                notificationService.getNotifications(userId, page, pageSize)));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<StandardResponse<Object>> markRead(
            @RequestBody NotificationMarkRequestDto request
    ) {
        return ResponseEntity.ok(StandardResponse.success(
                notificationService.markAsRead(request.getIds())));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<StandardResponse<UnreadCountDto>> getUnreadCount(
            @AuthenticationPrincipal UUID viewerUserId
    ) {
        var response = new UnreadCountDto(notificationService.getUnreadCount(viewerUserId));
        return ResponseEntity.ok(StandardResponse.success(response));
    }
}
