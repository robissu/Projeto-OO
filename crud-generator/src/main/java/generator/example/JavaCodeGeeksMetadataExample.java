package generator.example;

import framework.exceptions.DatabaseException;
import generator.sample.VetClinicSampleDatabase;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exemplo didatico inspirado no artigo "JDBC DatabaseMetaData Example" do
 * JavaCodeGeeks, adaptado para SQLite e para este projeto.
 *
 * <p>A intencao e mostrar a mecanica basica do DatabaseMetaData: obter a
 * conexao, chamar connection.getMetaData(), listar tabelas com getTables() e
 * listar colunas com getColumns().</p>
 */
public final class JavaCodeGeeksMetadataExample {

    private JavaCodeGeeksMetadataExample() {
        // utility class
    }

    /**
     * Executa o exemplo. Se o caminho for vazio, cria/usa a base de exemplo.
     */
    public static void run(String databasePathOrJdbcUrl) {
        var jdbcUrl = normalizeInput(databasePathOrJdbcUrl);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            DatabaseMetaData metadata = connection.getMetaData();

            printGeneralMetadata(metadata);
            List<String> tables = getTablesMetadata(metadata);
            getColumnsMetadata(metadata, tables);
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao executar exemplo DatabaseMetaData: " + e.getMessage(), e);
        }
    }

    private static void printGeneralMetadata(DatabaseMetaData metadata) throws SQLException {
        System.out.println();
        System.out.println("INFORMACOES GERAIS DO BANCO");
        System.out.println("Database Product Name    : " + metadata.getDatabaseProductName());
        System.out.println("Database Product Version : " + metadata.getDatabaseProductVersion());
        System.out.println("Logged User              : " + metadata.getUserName());
        System.out.println("JDBC Driver              : " + metadata.getDriverName());
        System.out.println("Driver Version           : " + metadata.getDriverVersion());
        System.out.println();
    }

    private static List<String> getTablesMetadata(DatabaseMetaData metadata) throws SQLException {
        var tables = new ArrayList<String>();
        String[] tableTypes = { "TABLE" };

        try (ResultSet rs = metadata.getTables(null, null, "%", tableTypes)) {
            while (rs.next()) {
                var tableName = rs.getString("TABLE_NAME");
                if (tableName != null && !tableName.startsWith("sqlite_")) {
                    tables.add(tableName);
                }
            }
        }

        return tables;
    }

    private static void getColumnsMetadata(DatabaseMetaData metadata, List<String> tables) throws SQLException {
        System.out.println("TABELAS E COLUNAS");

        if (tables.isEmpty()) {
            System.out.println("Nenhuma tabela encontrada.");
            return;
        }

        for (String table : tables) {
            System.out.println();
            System.out.println(table.toUpperCase());

            try (ResultSet rs = metadata.getColumns(null, null, table, "%")) {
                while (rs.next()) {
                    var columnName = rs.getString("COLUMN_NAME");
                    var typeName = rs.getString("TYPE_NAME");
                    var columnSize = rs.getString("COLUMN_SIZE");
                    var nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable ? "NULL" : "NOT NULL";

                    System.out.printf("  %-22s %-14s tamanho=%-6s %s%n",
                        columnName, typeName, columnSize, nullable);
                }
            }
        }
        System.out.println();
    }

    private static String normalizeInput(String databasePathOrJdbcUrl) {
        if (databasePathOrJdbcUrl == null || databasePathOrJdbcUrl.isBlank()) {
            Path created = VetClinicSampleDatabase.createDefault(true);
            System.out.println("Banco de exemplo criado em: " + created);
            return "jdbc:sqlite:" + created;
        }

        var value = databasePathOrJdbcUrl.trim();
        if (value.startsWith("jdbc:")) {
            return value;
        }

        var path = Path.of(value).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new DatabaseException("Arquivo do banco de dados nao encontrado: " + path);
        }
        return "jdbc:sqlite:" + path;
    }
}
