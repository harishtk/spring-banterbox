package space.banterbox.feature.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.exception.ProfileNotFoundException;
import space.banterbox.feature.user.mapper.UserMapper;
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

    public void follow(UUID currentUserId, UUID targetUserId) {
        var id = UsersFollowerId.builder()
                .followerId(currentUserId)
                .followingId(targetUserId)
                .build();

        boolean alreadyFollowing = followRepository.existsById(id);
        if (alreadyFollowing) {
            throw new IllegalStateException("Already following");
        }

        UsersFollower relationship = UsersFollower.builder()
                .id(id)
                .build();
        followRepository.save(relationship);
    }

    public void unfollow(UUID currentUserId, UUID targetUserId) {
        var id = UsersFollowerId.builder()
                .followerId(currentUserId)
                .followingId(targetUserId)
                .build();

        boolean alreadyFollowing = followRepository.existsById(id);
        if (!alreadyFollowing) {
            throw new IllegalStateException("Not following");
        }

        followRepository.deleteById(id);
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

    public UserProfileDto getProfile(UUID id) {
        return userRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
    }

    public Page<UserPreviewDto> getAllUsers(String sortBy, int page, int size) {
        return userRepository.getAllUsersWithPreview(PageRequest.of(page, size, Sort.by(sortBy)));
    }
}
