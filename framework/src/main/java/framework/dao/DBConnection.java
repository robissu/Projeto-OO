package framework.dao;

import framework.exceptions.ConnectionException;
import framework.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection – Singleton que gerencia a conexão JDBC com o banco de dados.
 *
 * <p>Uso:
 * <pre>
 *   DBConnection.getInstance().open("jdbc:sqlite:meu_banco.db");
 *   Connection c = DBConnection.getInstance().getConnection();
 * </pre>
 */
public class DBConnection {

    private static final DBConnection INSTANCE = new DBConnection();

    private Connection connection;

    private DBConnection() { /* singleton */ }

    public static DBConnection getInstance() {
        return INSTANCE;
    }

    /**
     * Abre a conexão com a URL JDBC informada.
     * Se já estiver aberta, não faz nada.
     *
     * @param jdbcUrl URL JDBC (ex: "jdbc:sqlite:vetclinic.db")
     */
    public void open(String jdbcUrl) {
        if (connection != null) return;
        try {
            connection = DriverManager.getConnection(jdbcUrl);
            // Habilita foreign keys no SQLite
            try (var st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        } catch (SQLException e) {
            throw new ConnectionException("Não foi possível conectar em: " + jdbcUrl, e);
        }
    }

    /** Retorna a conexão ativa. Lança exceção se não estiver aberta. */
    public Connection getConnection() {
        if (connection == null) {
            throw new ConnectionException(
                "Banco não aberto. Chame DBConnection.getInstance().open(url) primeiro.");
        }
        return connection;
    }

    /** Executa um comando DDL (CREATE TABLE, DROP TABLE, etc.). */
    public void execute(String sql) {
        try (Statement st = getConnection().createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao executar DDL: " + e.getMessage(), e);
        }
    }

    /** Fecha a conexão. */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // log apenas — não relançar no close
                System.err.println("[WARN] Erro ao fechar conexão: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    public boolean isOpen() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
