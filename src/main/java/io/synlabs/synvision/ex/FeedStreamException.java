package io.synlabs.synvision.ex;

/**
 * Created by blackcaps on 8/12/16.
 */
public class FeedStreamException extends RuntimeException {

    public FeedStreamException(String message){ super(message); }

    public FeedStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeedStreamException(Throwable cause) {
        super(cause);
    }

    public FeedStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FeedStreamException() {
    }
}
