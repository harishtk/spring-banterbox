package space.banterbox.feature.notification.projection;

import space.banterbox.feature.notification.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public interface NotificationProjection {
    UUID getId();
    NotificationType getNotificationType();
    String getSubType();
    String getMessage();
    UUID getReferenceId();
    Instant getCreatedAt();
    Boolean getRead();
    UUID getRecipientId();
    UUID getActorId();
    SenderInfo getActor();

    interface SenderInfo {
        UUID getId();
        String getUsername();
        String getDisplayName();
        String getProfilePictureId();
    }
}
