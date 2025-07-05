package space.banterbox.feature.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserProfileDto {
    private UUID id;
    private String username;
    private String displayName;
    private String bio;
    private String profilePictureId;
    private Instant createdAt;
}

