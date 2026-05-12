package generator.reader;

import framework.dao.DBConnection;
import generator.model.TableInfo;

import java.util.List;

/**
 * Mantido por compatibilidade com versões anteriores do projeto.
 *
 * <p>A leitura principal agora fica em {@link DatabaseMetaDataReader}, que recebe
 * uma {@link java.sql.Connection} e pode ser usada por qualquer fonte de entrada
 * baseada em JDBC. Novas integrações devem preferir usar
 * {@code generator.input.MetadataInputSource}.</p>
 */
@Deprecated
public class MetadataReader {

    /**
     * Lê todos os metadados usando a conexão atual do {@link DBConnection}.
     */
    public List<TableInfo> readAll() {
        var connection = DBConnection.getInstance().getConnection();
        return new DatabaseMetaDataReader(connection).readAll();
    }
}
