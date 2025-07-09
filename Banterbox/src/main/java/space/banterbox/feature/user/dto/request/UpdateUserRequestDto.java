package space.banterbox.feature.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequestDto {

    @Size(max = 30, message = "Display name must be less than 30 characters")
    private String displayName;

    @Size(max = 280, message = "Bio must be less than 280 characters")
    private String bio;
}
