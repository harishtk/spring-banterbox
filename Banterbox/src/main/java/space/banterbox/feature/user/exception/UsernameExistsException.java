package space.banterbox.feature.user.exception;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String s) {
        super(s);
    }
}
