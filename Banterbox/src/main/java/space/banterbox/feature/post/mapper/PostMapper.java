package space.banterbox.feature.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "authorId", source = "author.id")
    PostDto toDto(Post post);
}
