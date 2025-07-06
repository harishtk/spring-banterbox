package space.banterbox.feature.post.mapper;

import org.mapstruct.Mapper;
import space.banterbox.feature.post.dto.AuthorDto;
import space.banterbox.feature.post.projection.PostWithLikes;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto toDto(PostWithLikes.UserInfo userInfo);
}
