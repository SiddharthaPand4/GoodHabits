package io.synlabs.synvision.ex;

/**
 * Created by blackcaps on 8/12/16.
 */
public class DataConflictException extends RuntimeException {

    public DataConflictException(String message){ super(message); }

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataConflictException(Throwable cause) {
        super(cause);
    }

    public DataConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DataConflictException() {
    }
}
