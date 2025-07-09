package space.banterbox.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import space.banterbox.feature.post.dto.PostDto;
import space.banterbox.feature.post.dto.PostSummaryDto;
import space.banterbox.feature.post.mapper.PostMapper;
import space.banterbox.feature.post.projection.PostWithLikes;
import space.banterbox.feature.post.repository.PostRepository;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.mapper.UserMapper;
import space.banterbox.feature.user.projection.UserPreviewProjection;
import space.banterbox.feature.user.repository.UserRepository;
import space.banterbox.search.dto.SearchRequestDto;
import space.banterbox.search.dto.SearchResponseDto;
import space.banterbox.search.dto.SearchType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public SearchResponseDto search(SearchRequestDto request, UUID viewerUserId) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        final String query = request.getQuery().trim().toLowerCase();

        List<UserPreviewDto> users = Collections.emptyList();
        List<PostSummaryDto> posts = Collections.emptyList();
        int usersCount = 0;
        int postsCount = 0;

        if (request.getType() == SearchType.USER || request.getType() == SearchType.ALL) {
            Page<UserPreviewProjection> userPage = userRepository.searchByUsernameOrDisplayName(query, viewerUserId, pageable);
            users = userPage.getContent().stream()
                    .map(userMapper::toPreviewDto)
                    .toList();
            usersCount = (int) userPage.getTotalElements();
        }

        if (request.getType() == SearchType.POSTS || request.getType() == SearchType.ALL) {
            Page<PostWithLikes> postPage = postRepository.searchByContent(query, viewerUserId, pageable);
            posts = postPage.getContent().stream()
                    .map(postMapper::toSummaryDto)
                    .toList();
            postsCount = (int) postPage.getTotalElements();
        }

        return new SearchResponseDto(users, posts, usersCount, postsCount);
    }
}
