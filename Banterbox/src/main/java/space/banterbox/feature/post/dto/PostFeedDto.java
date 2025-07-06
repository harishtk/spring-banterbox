package space.banterbox.feature.post.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PostFeedDto(
    List<PostDto> posts,
    List<AuthorDto> users,
    PageMetadata page
) {
    public record PageMetadata(
            long totalPosts,
            int totalPages,
            int currentPage,
            int pageSize,
            boolean isLastPage
    ) {
        public static PageMetadata from(Page<?> page) {
            return new PageMetadata(
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.getNumber(),
                    page.getSize(),
                    page.isLast()
            );
        }
    }
}