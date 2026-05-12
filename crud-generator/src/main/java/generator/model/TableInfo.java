package generator.model;

import java.util.List;

/**
 * TableInfo – Metadados completos de uma tabela do banco de dados.
 *
 * @param tableName nome original da tabela (ex: "appointments")
 * @param columns   lista de colunas com seus metadados
 */
public record TableInfo(String tableName, List<ColumnInfo> columns) {

    /**
     * Nome da classe Java gerada a partir do nome da tabela.
     * Converte para singular e PascalCase.
     * Ex: "appointments" → "Appointment", "clientes" → "Cliente"
     */
    public String className() {
        var singular = toSingular(tableName.toLowerCase());
        return ColumnInfo.toCamelCase(singular, true);
    }

    /**
     * Nome da variável local (camelCase, minúscula).
     * Ex: "Appointment" → "appointment"
     */
    public String varName() {
        var name = className();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    /** Nome do arquivo de entidade: "Appointment.java" */
    public String entityFileName()  { return className() + ".java"; }

    /** Nome do arquivo DAO: "AppointmentDao.java" */
    public String daoFileName()     { return className() + "Dao.java"; }

    /** Nome do arquivo de exemplo: "AppointmentExemplo.java" */
    public String exemploFileName() { return className() + "Exemplo.java"; }

    /** Coluna de chave primária (primeira PK encontrada). */
    public ColumnInfo primaryKey() {
        return columns.stream()
            .filter(ColumnInfo::isPrimaryKey)
            .findFirst()
            .orElse(columns.isEmpty() ? null : columns.get(0));
    }

    /** Colunas que NÃO são auto-incrementadas (usadas em INSERT). */
    public List<ColumnInfo> insertableColumns() {
        return columns.stream()
            .filter(c -> !c.isAutoIncrement())
            .toList();
    }

    /** Colunas editáveis (não PK e não auto-increment; usadas em UPDATE). */
    public List<ColumnInfo> updatableColumns() {
        return columns.stream()
            .filter(c -> !c.isPrimaryKey() && !c.isAutoIncrement())
            .toList();
    }

    // ── singularizador simples ────────────────────────────────────────────

    private static String toSingular(String name) {
        if (name.endsWith("ies"))  return name.substring(0, name.length() - 3) + "y";
        if (name.endsWith("oes"))  return name.substring(0, name.length() - 2);
        if (name.endsWith("oes"))  return name.substring(0, name.length() - 2);
        if (name.endsWith("es") && name.length() > 3) {
            // "clientes" → "cliente", "vendas" → handled by 's' below
            var stem = name.substring(0, name.length() - 1);  // remove only 's'
            return stem;
        }
        if (name.endsWith("s") && name.length() > 2) {
            return name.substring(0, name.length() - 1);
        }
        return name;
    }
}
