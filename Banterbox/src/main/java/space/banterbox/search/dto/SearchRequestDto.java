package space.banterbox.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import space.banterbox.search.validation.EnumValidator;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDto {

    @JsonProperty("q")
    @NotNull(message = "Search query is required")
    @NotBlank(message = "Search query cannot be blank")
    private String query;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("size")
    private Integer pageSize;

    @EnumValidator(
            enumClazz = SearchType.class,
            message = "Search type must be one of: ALL, USER, POSTS")
    @JsonProperty("type")
    private SearchType type = SearchType.ALL;
}
