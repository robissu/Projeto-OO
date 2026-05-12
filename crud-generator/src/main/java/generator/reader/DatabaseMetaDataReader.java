package generator.reader;

import framework.exceptions.DatabaseException;
import generator.model.ColumnInfo;
import generator.model.TableInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lê metadados de tabelas a partir de uma {@link Connection} JDBC.
 *
 * <p>Esta classe segue a ideia do exemplo com {@link DatabaseMetaData}: primeiro
 * obtém o objeto de metadados com {@code connection.getMetaData()}, depois usa
 * métodos como:</p>
 *
 * <ul>
 *   <li>{@code getTables()} para listar tabelas;</li>
 *   <li>{@code getColumns()} para listar colunas e tipos;</li>
 *   <li>{@code getPrimaryKeys()} para identificar chaves primárias;</li>
 *   <li>{@code getImportedKeys()} para identificar chaves estrangeiras.</li>
 * </ul>
 *
 * <p>O resultado é convertido para os modelos internos {@link TableInfo} e
 * {@link ColumnInfo}, que depois são usados pelos writers do gerador.</p>
 */
public class DatabaseMetaDataReader {

    private final Connection connection;

    public DatabaseMetaDataReader(Connection connection) {
        if (connection == null) {
            throw new DatabaseException("Conexão JDBC não informada para leitura de metadados.");
        }
        this.connection = connection;
    }

    /**
     * Lê todas as tabelas comuns da base conectada.
     *
     * @return tabelas ordenadas por nome
     */
    public List<TableInfo> readAll() {
        try {
            var metaData = connection.getMetaData();
            var tableNames = readTableNames(metaData);
            var tables = new ArrayList<TableInfo>();

            for (var tableName : tableNames) {
                var primaryKeys = readPrimaryKeys(metaData, tableName);
                var foreignKeys = readForeignKeys(metaData, tableName);
                var columns = readColumns(metaData, tableName, primaryKeys, foreignKeys);
                tables.add(new TableInfo(tableName, columns));
            }

            tables.sort(Comparator.comparing(TableInfo::tableName));
            return tables;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao acessar DatabaseMetaData: " + e.getMessage(), e);
        }
    }

    /**
     * Lista tabelas do tipo TABLE.
     */
    private List<String> readTableNames(DatabaseMetaData metaData) throws SQLException {
        var tables = new ArrayList<String>();

        try (ResultSet rs = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
            while (rs.next()) {
                var tableName = rs.getString("TABLE_NAME");
                if (shouldIgnoreTable(tableName)) {
                    continue;
                }
                tables.add(tableName);
            }
        }

        return tables;
    }

    /**
     * Lê as colunas que compõem a chave primária da tabela.
     */
    private Set<String> readPrimaryKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
        var primaryKeys = new HashSet<String>();

        try (ResultSet rs = metaData.getPrimaryKeys(null, null, tableName)) {
            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }

        return primaryKeys;
    }

    /**
     * Lê as chaves estrangeiras importadas pela tabela.
     *
     * @return mapa coluna_fk -> informação da FK
     */
    private Map<String, ForeignKeyInfo> readForeignKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
        var foreignKeys = new HashMap<String, ForeignKeyInfo>();

        try (ResultSet rs = metaData.getImportedKeys(null, null, tableName)) {
            while (rs.next()) {
                var fkColumn = rs.getString("FKCOLUMN_NAME");
                var referencedTable = rs.getString("PKTABLE_NAME");
                var referencedColumn = rs.getString("PKCOLUMN_NAME");

                foreignKeys.put(
                    fkColumn.toLowerCase(),
                    new ForeignKeyInfo(referencedTable, referencedColumn)
                );
            }
        }

        return foreignKeys;
    }

    /**
     * Lê as colunas da tabela, já marcando PK, FK, nulidade e auto incremento.
     */
    private List<ColumnInfo> readColumns(
        DatabaseMetaData metaData,
        String tableName,
        Set<String> primaryKeys,
        Map<String, ForeignKeyInfo> foreignKeys
    ) throws SQLException {
        var columns = new ArrayList<ColumnInfo>();

        try (ResultSet rs = metaData.getColumns(null, null, tableName, "%")) {
            while (rs.next()) {
                var columnName = rs.getString("COLUMN_NAME");
                var typeName = rs.getString("TYPE_NAME");
                var nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                var autoIncrement = "YES".equalsIgnoreCase(safeGetString(rs, "IS_AUTOINCREMENT"));

                var javaTypes = ColumnInfo.mapJavaType(typeName);
                var primaryKey = primaryKeys.contains(columnName.toLowerCase());
                var foreignKey = foreignKeys.get(columnName.toLowerCase());

                // No SQLite, INTEGER PRIMARY KEY se comporta como rowid/autoincremento
                // mesmo quando o driver não marca IS_AUTOINCREMENT como YES.
                var effectiveAutoIncrement = autoIncrement || (primaryKey && "int".equals(javaTypes[0]));

                columns.add(new ColumnInfo(
                    columnName,
                    typeName,
                    javaTypes[0],
                    javaTypes[1],
                    primaryKey,
                    nullable,
                    foreignKey != null ? foreignKey.referencedTable() : null,
                    foreignKey != null ? foreignKey.referencedColumn() : null,
                    effectiveAutoIncrement
                ));
            }
        }

        return columns;
    }

    private boolean shouldIgnoreTable(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            return true;
        }

        // Tabelas internas comuns do SQLite.
        return tableName.startsWith("sqlite_");
    }

    /**
     * Alguns drivers JDBC não disponibilizam todas as colunas opcionais do
     * ResultSet de metadados. Este helper evita quebrar a leitura nesses casos.
     */
    private String safeGetString(ResultSet rs, String columnLabel) {
        try {
            return rs.getString(columnLabel);
        } catch (SQLException e) {
            return null;
        }
    }

    private record ForeignKeyInfo(String referencedTable, String referencedColumn) {}
}
