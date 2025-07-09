package space.banterbox.feature.notification.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import space.banterbox.feature.notification.model.Notification;
import space.banterbox.feature.notification.projection.NotificationProjection;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("""
        SELECT
            n.id as id,
            n.type as notificationType,
            n.subType as subType,
            n.message as message,
            n.referenceId as referenceId,
            n.createdAt as createdAt,
            n.read as read,
            n.recipient.id as recipientId,
            n.actorId as actorId,
            n.actor as actor
        FROM Notification n
        WHERE n.recipient.id = :recipientId
        ORDER BY n.createdAt DESC
    """)
    Page<NotificationProjection> findAllByRecipientId(@Param("recipientId") UUID recipientId, Pageable pageable);

    long countByActorIdEqualsAndRead(UUID actorId, Boolean read);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id IN :ids")
    void markAsRead(@Param("ids") List<UUID> ids);
}