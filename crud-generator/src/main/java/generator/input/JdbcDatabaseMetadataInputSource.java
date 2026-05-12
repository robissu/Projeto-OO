package generator.input;

import framework.exceptions.DatabaseException;
import generator.model.TableInfo;
import generator.reader.DatabaseMetaDataReader;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Entrada padrão do gerador: lê os metadados de uma base de dados usando JDBC.
 *
 * <p>Para SQLite, aceita tanto o caminho direto do arquivo:</p>
 * <pre>{@code
 * C:\\dados\\banco.db
 * }</pre>
 *
 * <p>quanto uma URL JDBC completa:</p>
 * <pre>{@code
 * jdbc:sqlite:C:\\dados\\banco.db
 * }</pre>
 */
public class JdbcDatabaseMetadataInputSource implements MetadataInputSource {

    private final String jdbcUrl;

    public JdbcDatabaseMetadataInputSource(String databasePathOrJdbcUrl) {
        this.jdbcUrl = normalizeJdbcUrl(databasePathOrJdbcUrl);
    }

    @Override
    public List<TableInfo> readTables() {
        try (var connection = DriverManager.getConnection(jdbcUrl)) {
            return new DatabaseMetaDataReader(connection).readAll();
        } catch (SQLException e) {
            throw new DatabaseException(
                "Erro ao ler metadados da base via JDBC: " + e.getMessage(), e
            );
        }
    }

    public String jdbcUrl() {
        return jdbcUrl;
    }

    private static String normalizeJdbcUrl(String databasePathOrJdbcUrl) {
        if (databasePathOrJdbcUrl == null || databasePathOrJdbcUrl.isBlank()) {
            throw new DatabaseException("Caminho/URL do banco de dados não informado.");
        }

        var value = databasePathOrJdbcUrl.trim();
        if (value.startsWith("jdbc:")) {
            return value;
        }

        try {
            var path = Path.of(value);
            if (!Files.exists(path)) {
                throw new DatabaseException("Arquivo do banco de dados nao encontrado: " + path.toAbsolutePath());
            }
            return "jdbc:sqlite:" + path.toAbsolutePath();
        } catch (InvalidPathException e) {
            throw new DatabaseException("Caminho do banco de dados invalido: " + value, e);
        }
    }
}
