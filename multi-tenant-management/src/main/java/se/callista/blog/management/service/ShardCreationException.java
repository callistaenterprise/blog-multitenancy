package se.callista.blog.management.service;

public class ShardCreationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ShardCreationException(String message) {
        super(message);
    }

    public ShardCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShardCreationException(Throwable cause) {
        super(cause);
    }
}
