package space.banterbox.feature.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPreviewDto {
    private UUID id;
    private String username;
    private String displayName;
    private String profilePictureId;
}
