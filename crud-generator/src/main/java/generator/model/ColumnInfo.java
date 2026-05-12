package generator.model;

/**
 * ColumnInfo – Metadados de uma coluna lidos via {@link java.sql.DatabaseMetaData}.
 *
 * @param columnName   nome original da coluna no banco
 * @param jdbcTypeName nome do tipo SQL (ex: "INTEGER", "TEXT", "REAL")
 * @param javaType     tipo Java mapeado (ex: "int", "String", "double")
 * @param boxedType    tipo Java encaixotado para generics (ex: "Integer", "String")
 * @param isPrimaryKey true se esta coluna é chave primária
 * @param isNullable   true se aceita NULL
 * @param fkTable      tabela referenciada (se for FK), ou null
 * @param fkColumn     coluna referenciada (se for FK), ou null
 * @param isAutoIncrement true se é gerada automaticamente pelo banco
 */
public record ColumnInfo(
    String  columnName,
    String  jdbcTypeName,
    String  javaType,
    String  boxedType,
    boolean isPrimaryKey,
    boolean isNullable,
    String  fkTable,
    String  fkColumn,
    boolean isAutoIncrement
) {
    /** Nome do campo Java em camelCase (ex: "owner_id" → "ownerId"). */
    public String fieldName() {
        return toCamelCase(columnName, false);
    }

    /** Nome do getter Java (ex: "ownerId" → "getOwnerId"). */
    public String getterName() {
        var f = fieldName();
        return "get" + Character.toUpperCase(f.charAt(0)) + f.substring(1);
    }

    /** Nome do setter Java (ex: "ownerId" → "setOwnerId"). */
    public String setterName() {
        var f = fieldName();
        return "set" + Character.toUpperCase(f.charAt(0)) + f.substring(1);
    }

    /** true se esta coluna é uma chave estrangeira. */
    public boolean isForeignKey() {
        return fkTable != null && !fkTable.isBlank();
    }

    // ── helpers estáticos de conversão ────────────────────────────────────

    /**
     * Converte nome com underscores para camelCase.
     * Ex: "owner_id" → "ownerId" (capitalize=false) ou "OwnerId" (capitalize=true).
     */
    public static String toCamelCase(String name, boolean capitalizeFirst) {
        var sb = new StringBuilder();
        boolean nextUpper = capitalizeFirst;
        for (char c : name.toLowerCase().toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                sb.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }

    /**
     * Mapeia tipo JDBC/SQL para tipo Java primitivo/String.
     * Retorna um array de dois elementos: [tipo primitivo, tipo encaixotado].
     */
    public static String[] mapJavaType(String jdbcType) {
        return switch (jdbcType.toUpperCase().split("\\(")[0].trim()) {
            case "INTEGER", "INT", "SMALLINT", "TINYINT", "MEDIUMINT" -> new String[]{"int",    "Integer"};
            case "BIGINT"                                              -> new String[]{"long",   "Long"};
            case "REAL", "FLOAT", "DOUBLE", "DOUBLE PRECISION"        -> new String[]{"double", "Double"};
            case "NUMERIC", "DECIMAL"                                  -> new String[]{"double", "Double"};
            case "BOOLEAN", "BOOL"                                     -> new String[]{"boolean","Boolean"};
            case "BLOB"                                                -> new String[]{"byte[]", "byte[]"};
            // TEXT, VARCHAR, CHAR, CLOB, DATE, DATETIME, TIMESTAMP, etc. → String
            default                                                    -> new String[]{"String", "String"};
        };
    }
}
