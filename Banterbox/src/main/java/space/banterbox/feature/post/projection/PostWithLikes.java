package space.banterbox.feature.post.projection;

import java.time.Instant;
import java.util.UUID;

public interface PostWithLikes {
    UUID getId();
    UUID getAuthorId();
    String getContent();
    Instant getCreatedAt();
    Instant getUpdatedAt();
    Long getLikesCount();
    Boolean getLikedByCurrentUser();
    UserInfo getAuthor();

    interface UserInfo {
        UUID getId();
        String getUsername();
        String getDisplayName();
        String getProfilePictureId();
    }
}
