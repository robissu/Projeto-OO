package framework.exceptions;

/** Lançada quando não é possível abrir ou manter a conexão com o banco. */
public class ConnectionException extends DatabaseException {

    public ConnectionException(String message) {
        super("[Connection] " + message);
    }

    public ConnectionException(String message, Throwable cause) {
        super("[Connection] " + message, cause);
    }
}
