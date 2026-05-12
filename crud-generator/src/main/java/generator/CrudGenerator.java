package generator;

import framework.exceptions.FrameworkException;
import generator.model.TableInfo;
import generator.input.JdbcDatabaseMetadataInputSource;
import generator.input.MetadataInputSource;
import generator.writer.CodeFileWriter;
import generator.writer.DaoWriter;
import generator.writer.EntityWriter;
import generator.writer.ExampleWriter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CrudGenerator – Orquestrador principal do gerador.
 *
 * <p>Fluxo:
 * <ol>
 *   <li>Lê metadados pela fonte de entrada configurada</li>
 *   <li>A fonte padrão usa JDBC com {@code DatabaseMetaData}</li>
 *   <li>Para cada tabela, gera Entidade, DAO e Exemplo</li>
 *   <li>Grava os arquivos no diretório de saída via {@link CodeFileWriter}</li>
 * </ol>
 *
 * <p>Pacotes gerados:
 * <ul>
 *   <li>{@code generated.domain} — classes de entidade</li>
 *   <li>{@code generated.dao}    — classes DAO</li>
 *   <li>{@code generated.exemplo}— classes de exemplo</li>
 * </ul>
 */
public class CrudGenerator {

    // Pacotes das classes geradas
    public static final String PKG_ENTITY  = "generated.domain";
    public static final String PKG_DAO     = "generated.dao";
    public static final String PKG_EXAMPLE = "generated.exemplo";

    private final MetadataInputSource inputSource;
    private final Path outputRoot;

    /**
     * @param dbPath    caminho para o arquivo de banco (ex: "banco.db" ou "/home/user/bd.db")
     * @param outputDir diretório onde o código será gerado
     */
    public CrudGenerator(String dbPath, String outputDir) {
        this(new JdbcDatabaseMetadataInputSource(dbPath), outputDir);
    }

    /**
     * Permite usar outras fontes de metadados no futuro, por exemplo JSON/YAML,
     * sem alterar os writers de entidade, DAO e exemplo.
     *
     * @param inputSource fonte de metadados das tabelas
     * @param outputDir diretório onde o código será gerado
     */
    public CrudGenerator(MetadataInputSource inputSource, String outputDir) {
        this.inputSource = inputSource;
        this.outputRoot = Path.of(outputDir);
    }

    /**
     * Executa o processo completo de geração.
     *
     * @return lista de arquivos gerados
     * @throws FrameworkException em caso de erro de conexão ou I/O
     */
    public List<Path> generate() {
        // 1. Ler metadados a partir da fonte de entrada configurada.
        // A opção padrão é JDBC/DatabaseMetaData, mas outras fontes podem
        // implementar MetadataInputSource no futuro.
        var tables = inputSource.readTables();

        if (tables.isEmpty()) {
            System.out.println("  [AVISO] Nenhuma tabela encontrada no banco informado.");
            return List.of();
        }

        // 2. Instanciar escritores
        var entityWriter  = new EntityWriter(PKG_ENTITY);
        var daoWriter     = new DaoWriter(PKG_ENTITY, PKG_DAO);
        var exampleWriter = new ExampleWriter(PKG_ENTITY, PKG_DAO, PKG_EXAMPLE, tables);
        var fileWriter    = new CodeFileWriter(outputRoot);

        var generated = new ArrayList<Path>();

        System.out.println("\n  Gerando código para " + tables.size() + " tabela(s)...\n");

        // 3. Gerar arquivos para cada tabela
        for (var table : tables) {
            System.out.println("  → Tabela: " + table.tableName()
                + "  (classe: " + table.className() + ")");

            // Entidade
            var entityCode = entityWriter.generate(table);
            var entityPath = fileWriter.write(PKG_ENTITY, table.entityFileName(), entityCode);
            generated.add(entityPath);
            System.out.println("    ✓ " + table.entityFileName());

            // DAO
            var daoCode = daoWriter.generate(table);
            var daoPath = fileWriter.write(PKG_DAO, table.daoFileName(), daoCode);
            generated.add(daoPath);
            System.out.println("    ✓ " + table.daoFileName());

            // Exemplo
            var exCode  = exampleWriter.generate(table);
            var exPath  = fileWriter.write(PKG_EXAMPLE, table.exemploFileName(), exCode);
            generated.add(exPath);
            System.out.println("    ✓ " + table.exemploFileName());

            System.out.println();
        }

        return generated;
    }

    /** Lista as tabelas do banco sem gerar código (modo de pré-visualização). */
    public List<TableInfo> preview() {
        return inputSource.readTables();
    }
}
