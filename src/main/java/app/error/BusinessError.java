package app.error;

import app.exception.UserException;
import lombok.Getter;

@Getter
public class BusinessError extends RestError {
    private String messageKey;
    private String[] arguments;

    public BusinessError(String messageKey, String... arguments) {
        this.messageKey = messageKey;
        this.arguments = arguments;
    }

    @Override
    public UserException getException() {
        return new UserException(messageKey, arguments);
    }
}