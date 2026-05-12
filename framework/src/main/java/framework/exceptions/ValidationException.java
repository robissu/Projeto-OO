package framework.exceptions;

/** Lançada quando uma regra de negócio ou validação de entrada é violada. */
public class ValidationException extends FrameworkException {

    public ValidationException(String message) {
        super("[Validação] " + message);
    }
}
