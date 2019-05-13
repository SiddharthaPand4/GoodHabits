package io.synlabs.atcc.ex;

/**
 * Created by sushil on 13-08-2016.
 */
public class EdcastEnrollException extends RuntimeException {

    public EdcastEnrollException() {
    }

    public EdcastEnrollException(String message) {
        super(message);
    }

    public EdcastEnrollException(String message, Throwable cause) {
        super(message, cause);
    }

    public EdcastEnrollException(Throwable cause) {
        super(cause);
    }

    public EdcastEnrollException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
