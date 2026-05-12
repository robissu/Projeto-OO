package framework.dao;

import java.util.List;
import java.util.Optional;

/**
 * IDao&lt;T, ID&gt; – Interface genérica para operações CRUD.
 *
 * <p>Toda classe DAO concreta da aplicação deve implementar esta interface,
 * garantindo um contrato uniforme independentemente da entidade gerenciada.
 *
 * @param <T>  tipo da entidade (ex: Owner, Pet)
 * @param <ID> tipo da chave primária (ex: Integer, String)
 */
public interface IDao<T, ID> {

    /** Insere uma nova entidade no banco e retorna o ID gerado. */
    ID insert(T entity);

    /** Atualiza uma entidade já existente. */
    void update(T entity);

    /** Remove a entidade pelo identificador. */
    void delete(ID id);

    /** Busca por ID; retorna Optional vazio se não encontrado. */
    Optional<T> findById(ID id);

    /** Retorna todas as entidades da tabela. */
    List<T> findAll();
}
