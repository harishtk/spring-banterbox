package space.banterbox.feature.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Data
public class PostDto {
    private UUID id;
    private UUID authorId;
    private String authorUsername;
    private String content;
    Instant createdAt;
}
