package space.banterbox.feature.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import space.banterbox.feature.notification.dto.NotificationDto;
import space.banterbox.feature.notification.model.Notification;
import space.banterbox.feature.notification.projection.NotificationProjection;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDto toDto(Notification notification);

    @Mapping(target = "type", source = "notificationType")
    NotificationDto toDto(NotificationProjection notificationProjection);

}
