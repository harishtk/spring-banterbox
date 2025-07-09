package space.banterbox.feature.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import space.banterbox.feature.user.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.projection.UserPreviewProjection;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "followersCount", ignore = true)
    @Mapping(target = "followingCount", ignore = true)
    @Mapping(target = "isFollowing", ignore = true)
    @Mapping(target = "isSelf", ignore = true)
    UserProfileDto toDto(User user);

    UserPreviewDto toPreviewDto(User user);

    @Mapping(target = "isFollowing", source = "followedByCurrentUser")
    UserPreviewDto toPreviewDto(UserPreviewProjection userPreviewProjection);

    void updateUser(UpdateUserRequestDto updateUserRequestDto, @MappingTarget User user);
}
