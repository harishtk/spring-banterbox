package space.banterbox.feature.user.mapper;

import org.mapstruct.Mapper;
import space.banterbox.feature.user.dto.response.ProfileDto;
import space.banterbox.feature.user.model.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileDto toDto(Profile profile);
}
