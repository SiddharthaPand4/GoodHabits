package io.synlabs.synvision.ex;

/**
 * Created by sushil on 17-08-2016.
 */
public class RateException extends RuntimeException {
    public RateException() {
    }

    public RateException(String message) {
        super(message);
    }

    public RateException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateException(Throwable cause) {
        super(cause);
    }

    public RateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}