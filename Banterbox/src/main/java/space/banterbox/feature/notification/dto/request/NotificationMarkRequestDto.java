package space.banterbox.feature.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class NotificationMarkRequestDto {

    private final List<UUID> ids = List.of();

}
