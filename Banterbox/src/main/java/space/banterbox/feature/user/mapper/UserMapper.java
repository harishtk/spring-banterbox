package space.banterbox.feature.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import space.banterbox.feature.user.dto.request.CreateUserRequestDto;
import space.banterbox.feature.user.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.user.dto.response.UserResponseDto;
import space.banterbox.feature.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(CreateUserRequestDto createUserRequestDto);

    void updateUser(UpdateUserRequestDto updateUserRequestDto, @MappingTarget User user);
}
