package space.banterbox.feature.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import space.banterbox.feature.users.dto.request.CreateUserRequestDto;
import space.banterbox.feature.users.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.users.dto.response.UserResponseDto;
import space.banterbox.feature.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(CreateUserRequestDto createUserRequestDto);

    void updateUser(UpdateUserRequestDto updateUserRequestDto, @MappingTarget User user);
}
