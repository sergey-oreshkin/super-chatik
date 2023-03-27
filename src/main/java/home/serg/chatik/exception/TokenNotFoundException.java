package home.serg.chatik.exception;

public class TokenNotFoundException extends AuthorizationException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
