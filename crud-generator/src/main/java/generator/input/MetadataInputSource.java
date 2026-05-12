package generator.input;

import generator.model.TableInfo;

import java.util.List;

/**
 * Fonte de metadados utilizada pelo gerador.
 *
 * <p>Essa interface permite que o gerador use diferentes entradas no futuro,
 * sem alterar as classes que escrevem Entidades, DAOs e Exemplos. A opção
 * padrão do projeto é {@link JdbcDatabaseMetadataInputSource}, que lê um banco
 * via JDBC/DatabaseMetaData.</p>
 *
 * <p>Exemplos de futuras implementações possíveis:</p>
 * <ul>
 *   <li>leitura de um arquivo JSON com definição de tabelas;</li>
 *   <li>leitura de um arquivo YAML;</li>
 *   <li>leitura de outro SGBD via JDBC.</li>
 * </ul>
 */
@FunctionalInterface
public interface MetadataInputSource {

    /**
     * Lê e retorna as tabelas disponíveis na fonte de entrada.
     *
     * @return lista de tabelas com colunas, tipos, PKs e FKs
     */
    List<TableInfo> readTables();
}
