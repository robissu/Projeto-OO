package framework.dao;

import framework.exceptions.DatabaseException;
import framework.util.SqlHelper.SqlBinder;
import framework.util.SqlHelper.SqlMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AbstractDao&lt;T, ID&gt; — Classe base abstrata para DAOs JDBC.
 *
 * <p>Fornece três métodos protegidos que encapsulam todo o boilerplate
 * de {@link PreparedStatement}:
 * <ul>
 *   <li>{@link #executeUpdate} — INSERT / UPDATE / DELETE</li>
 *   <li>{@link #query}        — SELECT com lista de resultados</li>
 *   <li>{@link #queryOne}     — SELECT com resultado único (Optional)</li>
 * </ul>
 *
 * @param <T>  tipo da entidade gerenciada
 * @param <ID> tipo da chave primária
 */
public abstract class AbstractDao<T, ID> implements IDao<T, ID> {

    /** Atalho para a conexão ativa. */
    protected Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helpers protegidos
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Executa INSERT / UPDATE / DELETE.
     *
     * @param sql    SQL parametrizado com '?'
     * @param binder define os parâmetros (pode ser null)
     * @return ID gerado automaticamente (relevante em INSERTs)
     */
    protected int executeUpdate(String sql, SqlBinder binder) {
        try (var ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (binder != null) binder.bind(ps);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Falha ao executar: " + sql + "\n→ " + e.getMessage(), e);
        }
    }

    /**
     * Executa SELECT e mapeia todas as linhas para uma lista de T.
     *
     * @param sql       SQL parametrizado
     * @param binder    parâmetros (pode ser null para queries sem WHERE)
     * @param rowMapper converte cada linha do ResultSet em um T
     */
    protected List<T> query(String sql, SqlBinder binder, SqlMapper<T> rowMapper) {
        try (var ps = conn().prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            try (var rs = ps.executeQuery()) {
                var list = new ArrayList<T>();
                while (rs.next()) list.add(rowMapper.map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Falha na query: " + sql + "\n→ " + e.getMessage(), e);
        }
    }

    /**
     * Executa SELECT que retorna no máximo uma linha.
     *
     * @return Optional com o objeto mapeado, ou vazio se não encontrado
     */
    protected Optional<T> queryOne(String sql, SqlBinder binder, SqlMapper<T> rowMapper) {
        var rows = query(sql, binder, rowMapper);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }
}
