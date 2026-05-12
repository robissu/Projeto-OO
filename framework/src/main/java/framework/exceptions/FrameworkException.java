package framework.exceptions;

/**
 * Exceção base do framework. Todas as exceções específicas estendem esta.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super("[Framework] " + message);
    }

    public FrameworkException(String message, Throwable cause) {
        super("[Framework] " + message, cause);
    }
}
