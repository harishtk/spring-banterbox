package space.banterbox.feature.user.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import space.banterbox.feature.user.model.Role;

@Data
public class UpdateUserRequestDto {

    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Size(max = 255, message = "Display name must be less than 255 characters")
    private String displayName;

    @Size(max = 280, message = "Bio must be less than 280 characters")
    private String bio;
}
