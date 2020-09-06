package com.piedpiper.goodhabits.ex;

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
