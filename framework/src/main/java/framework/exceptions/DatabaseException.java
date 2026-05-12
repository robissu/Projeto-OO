package framework.exceptions;

/** Lançada quando uma operação no banco de dados falha. */
public class DatabaseException extends FrameworkException {

    public DatabaseException(String message) {
        super("[DB] " + message);
    }

    public DatabaseException(String message, Throwable cause) {
        super("[DB] " + message, cause);
    }
}
