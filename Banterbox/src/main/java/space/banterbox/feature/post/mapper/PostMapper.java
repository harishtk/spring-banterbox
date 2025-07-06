package space.banterbox.feature.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.projection.PostWithLikes;
import space.banterbox.feature.post.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "likedByCurrentUser", ignore = true)
    PostDto toDto(Post post);

    PostDto toDto(PostWithLikes postWithLikes);

}
