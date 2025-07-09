package space.banterbox.feature.user.projection;

public interface UserPreviewProjection {
    String getId();
    String getUsername();
    String getDisplayName();
    String getProfilePictureId();
    Boolean getFollowedByCurrentUser();
}
