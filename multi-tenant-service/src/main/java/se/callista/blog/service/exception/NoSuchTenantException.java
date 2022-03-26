package se.callista.blog.service.exception;

public class NoSuchTenantException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoSuchTenantException(String message) {
        super(message);
    }

    public NoSuchTenantException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchTenantException(Throwable cause) {
        super(cause);
    }
}
