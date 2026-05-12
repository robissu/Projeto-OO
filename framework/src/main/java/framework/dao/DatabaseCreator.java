package framework.dao;

import framework.exceptions.DatabaseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * DatabaseCreator - utilitario do framework para criar bancos SQLite a partir
 * de comandos SQL.
 *
 * <p>O projeto original ja criava um banco SQLite ao abrir a conexao com
 * {@link DBConnection} e executar DDLs. Esta classe deixa esse comportamento
 * reutilizavel por outras aplicacoes, incluindo o gerador de CRUDs.</p>
 */
public final class DatabaseCreator {

    private DatabaseCreator() {
        // utility class
    }

    /**
     * Cria um banco SQLite executando uma lista de comandos SQL.
     *
     * @param databasePath caminho do arquivo .db
     * @param sqlStatements comandos SQL, como CREATE TABLE e INSERT
     * @param overwrite se true, remove o arquivo anterior antes de criar
     * @return caminho absoluto do banco criado
     */
    public static Path createSQLiteDatabase(Path databasePath, List<String> sqlStatements, boolean overwrite) {
        if (databasePath == null) {
            throw new DatabaseException("Caminho do banco nao informado.");
        }
        if (sqlStatements == null || sqlStatements.isEmpty()) {
            throw new DatabaseException("Nenhum comando SQL informado para criacao do banco.");
        }

        try {
            var absolutePath = databasePath.toAbsolutePath().normalize();
            var parent = absolutePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (overwrite) {
                Files.deleteIfExists(absolutePath);
            }

            var jdbcUrl = toSQLiteJdbcUrl(absolutePath);
            try (var connection = DriverManager.getConnection(jdbcUrl);
                 Statement statement = connection.createStatement()) {

                statement.execute("PRAGMA foreign_keys = ON");

                for (var sql : sqlStatements) {
                    if (sql != null && !sql.isBlank()) {
                        statement.execute(sql);
                    }
                }
            }

            return absolutePath;
        } catch (IOException e) {
            throw new DatabaseException("Erro ao preparar o arquivo do banco: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar banco SQLite: " + e.getMessage(), e);
        }
    }

    /**
     * Converte um Path em URL JDBC SQLite.
     */
    public static String toSQLiteJdbcUrl(Path databasePath) {
        if (databasePath == null) {
            throw new DatabaseException("Caminho do banco nao informado.");
        }
        return "jdbc:sqlite:" + databasePath.toAbsolutePath().normalize();
    }
}
