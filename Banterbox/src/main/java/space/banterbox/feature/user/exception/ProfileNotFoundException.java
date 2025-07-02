package space.banterbox.feature.user.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String s) {
        super(s);
    }
}
