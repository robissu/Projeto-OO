package framework.exceptions;

/** Lançada quando uma entidade não é encontrada pelo identificador informado. */
public class NotFoundException extends FrameworkException {

    public NotFoundException(String entity, Object id) {
        super("[Não encontrado] " + entity + " com id=" + id + " não existe.");
    }
}
