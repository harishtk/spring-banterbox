package space.banterbox.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.banterbox.core.response.StandardResponse;
import space.banterbox.search.dto.SearchRequestDto;
import space.banterbox.search.dto.SearchResponseDto;
import space.banterbox.search.service.SearchService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<StandardResponse<SearchResponseDto>> search(
            @AuthenticationPrincipal UUID viewerUserId,
            @RequestBody SearchRequestDto request) {
        return ResponseEntity.ok(StandardResponse.success(searchService.search(request, viewerUserId)));
    }
}
