package space.banterbox.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StandardResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    public static <T> StandardResponse<T> success(T data) {
        return new StandardResponse<>(HttpStatus.OK.value(), "Success", data);
    }

    public static <T> StandardResponse<T> of(int statusCode, String message, T data) {
        return new StandardResponse<>(statusCode, message, data);
    }
}
