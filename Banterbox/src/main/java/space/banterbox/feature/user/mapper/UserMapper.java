package space.banterbox.feature.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import space.banterbox.feature.user.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileDto toDto(User user);

    void updateUser(UpdateUserRequestDto updateUserRequestDto, @MappingTarget User user);
}
