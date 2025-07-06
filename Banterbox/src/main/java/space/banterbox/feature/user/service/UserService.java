package space.banterbox.feature.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.exception.UserFollowException;
import space.banterbox.feature.user.exception.UserNotFoundException;
import space.banterbox.feature.user.mapper.UserMapper;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.model.UsersFollower;
import space.banterbox.feature.user.model.UsersFollowerId;
import space.banterbox.feature.user.repository.FollowRepository;
import space.banterbox.feature.user.repository.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;

    public UserProfileDto follow(UUID currentUserId, UUID targetUserId) {
        var id = UsersFollowerId.builder()
                .followerId(currentUserId)
                .followingId(targetUserId)
                .build();

        boolean alreadyFollowing = followRepository.existsById(id);
        if (alreadyFollowing) {
            throw new UserFollowException("Already following");
        }

        var follower = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("follower not found"));
        var following = userRepository.findById(targetUserId).orElseThrow(() -> new UserNotFoundException("following not found"));

        UsersFollower relationship = UsersFollower.builder()
                .id(id)
                .follower(follower)
                .following(following)
                .build();
        followRepository.save(relationship);

        // Return the updated target user profile
        return enrichUserProfile(following, follower);
    }

    public UserProfileDto unfollow(UUID currentUserId, UUID targetUserId) {
        var id = UsersFollowerId.builder()
                .followerId(currentUserId)
                .followingId(targetUserId)
                .build();

        boolean alreadyFollowing = followRepository.existsById(id);
        if (!alreadyFollowing) {
            throw new UserFollowException("Not following");
        }

        followRepository.deleteById(id);

        // Return the updated target user profile
        var follower = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("follower not found"));
        var following = userRepository.findById(targetUserId).orElseThrow(() -> new UserNotFoundException("following not found"));
        return enrichUserProfile(following, follower);
    }

    public boolean isFollowing(UUID currentUserId, UUID targetUserId) {
        var id = UsersFollowerId.builder()
                .followerId(currentUserId)
                .followingId(targetUserId)
                .build();
        return followRepository.existsById(id);
    }

    public Page<UserPreviewDto> getFollowers(UUID userId, int page, int size) {
        return followRepository.findFollowersOf(userId, PageRequest.of(page, size));
    }

    public Page<UserPreviewDto> getFollowing(UUID userId, int page, int size) {
        return followRepository.findFollowingOf(userId, PageRequest.of(page, size));
    }

    public Page<UserPreviewDto> getAllUsers(String sortBy, int page, int size) {
        return userRepository.getAllUsersWithPreview(PageRequest.of(page, size, Sort.by(sortBy)));
    }

    public UserProfileDto getUserProfileByUsername(String username, UUID currentUserId) {
        User targetUser = userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return enrichUserProfile(targetUser, currentUser);
    }

    public UserProfileDto enrichUserProfile(User targetUser, User currentUser) {
        UserProfileDto base = userMapper.toDto(targetUser);

        boolean isSelf = currentUser.getId().equals(targetUser.getId());
        boolean isFollowing = !isSelf && followRepository.existsById(
                new UsersFollowerId(currentUser.getId(), targetUser.getId())
        );
        long followers = followRepository.countUsersFollowersByFollowingId(targetUser.getId());
        long following = followRepository.countUsersFollowingByFollowerId(targetUser.getId());

        return new UserProfileDto(
                base.getId(),
                base.getUsername(),
                base.getDisplayName(),
                base.getBio(),
                base.getProfilePictureId(),
                base.getCreatedAt(),
                followers,
                following,
                isFollowing,
                isSelf
        );
    }

    public UserProfileDto enrichUserProfile(UUID targetUserId, UUID currentUserId) {
        if (targetUserId.equals(currentUserId)) {
            User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
            return enrichUserProfile(currentUser, currentUser);
        } else {
            User targetUser = userRepository.findById(targetUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
            User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
            return enrichUserProfile(targetUser, currentUser);
        }
    }
}
