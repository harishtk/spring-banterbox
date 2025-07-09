package space.banterbox.feature.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.banterbox.feature.notification.model.Notification;
import space.banterbox.feature.notification.model.NotificationType;
import space.banterbox.feature.notification.repository.NotificationRepository;
import space.banterbox.feature.notification.service.NotificationService;
import space.banterbox.feature.post.model.Post;
import space.banterbox.feature.post.repository.PostRepository;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.repository.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationManager {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void createFollowedNotification(User follower, User followed) {
        var notification = new Notification();
        notification.setRecipient(followed);
        notification.setActorId(follower.getId());
        notification.setType(NotificationType.FOLLOW);
        notification.setReferenceId(follower.getId());
        notification.setMessage(follower.getUsername() + " started following you");
        notificationRepository.save(notification);
    }

    public void createFollowingNotification(User follower, User followed) {
        var notification = new Notification();
        notification.setRecipient(follower);
        notification.setActorId(followed.getId());
        notification.setType(NotificationType.FOLLOW);
        notification.setReferenceId(followed.getId());
        notification.setMessage("You are now following " + followed.getUsername());
        notificationRepository.save(notification);
    }

    public void createFollowedNotification(UUID followerUserId, UUID followedUserId) {
        var followerUser = userRepository.findById(followerUserId).orElseThrow();
        var followedUser = userRepository.findById(followedUserId).orElseThrow();
        createFollowedNotification(followerUser, followedUser);
    }

    public void createFollowingNotification(UUID followerUserId, UUID followedUserId) {
        var followerUser = userRepository.findById(followerUserId).orElseThrow();
        var followedUser = userRepository.findById(followedUserId).orElseThrow();
        createFollowingNotification(followerUser, followedUser);
    }

    public void createPostLikedNotification(UUID viewerUserId,
                                            UUID postId) {
        var viewer = userRepository.findById(viewerUserId).orElseThrow();
        var post = postRepository.findById(postId).orElseThrow();
        createPostLikedNotification(viewer, post);
    }

    public void createPostLikedNotification(User viewer, Post post) {
        var notification = new Notification();
        notification.setRecipient(post.getAuthor());
        notification.setActorId(viewer.getId());
        notification.setType(NotificationType.LIKE);
        notification.setReferenceId(post.getId());

        var postContent = post.getContent().substring(0, Math.min(post.getContent().length(), 20));
        notification.setMessage(viewer.getUsername() + " liked your post '" + postContent + "'");
        notificationRepository.save(notification);
    }
}
