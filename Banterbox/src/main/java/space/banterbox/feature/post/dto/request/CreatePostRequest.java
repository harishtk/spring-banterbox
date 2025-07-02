package space.banterbox.feature.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 280, message = "Content must be less than 280 characters")
    private String content;
}
