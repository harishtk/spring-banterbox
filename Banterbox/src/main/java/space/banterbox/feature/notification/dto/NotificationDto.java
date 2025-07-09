package space.banterbox.feature.notification.dto;

import space.banterbox.feature.notification.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        NotificationType type,
        String subType,
        String message,
        UUID referenceId,
        Instant createdAt,
        boolean read,
        UUID recipientId,
        UUID actorId,
        SenderDto actor
) {}

