package space.banterbox.feature.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileDto {
    private String id;
    private String displayName;
    private String bio;
    private String profilePictureId;
    private String createdAt;
}

