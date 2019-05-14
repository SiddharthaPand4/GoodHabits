package io.synlabs.atcc.ex;

/**
 * Created by blackcaps on 8/12/16.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message){ super(message); }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileStorageException(Throwable cause) {
        super(cause);
    }

    public FileStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FileStorageException() {
    }
}
