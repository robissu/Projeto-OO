package generator.writer;

import generator.model.ColumnInfo;
import generator.model.TableInfo;

/**
 * EntityWriter – Gera o código-fonte da classe de entidade (domínio).
 *
 * <p>Exemplo de saída para a tabela {@code owners}:
 * <pre>
 * public class Owner {
 *     private int    id;
 *     private String name;
 *     // getters, setters, toString...
 * }
 * </pre>
 */
public class EntityWriter {

    private final String targetPackage;

    public EntityWriter(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    /**
     * Gera o conteúdo completo da classe de entidade para a tabela informada.
     *
     * @param table metadados da tabela
     * @return código-fonte Java como String
     */
    public String generate(TableInfo table) {
        var sb = new StringBuilder();
        var className = table.className();

        // Cabeçalho do arquivo
        appendHeader(sb, table);

        // Campos privados
        sb.append("\n    // ── Campos ──────────────────────────────────────────────────────────\n");
        for (var col : table.columns()) {
            sb.append("    private ").append(col.javaType()).append(" ")
              .append(col.fieldName()).append(";\n");
        }

        // Construtores
        appendConstructors(sb, table, className);

        // Getters e setters
        appendGettersSetters(sb, table);

        // toString
        appendToString(sb, table, className);

        sb.append("}\n");
        return sb.toString();
    }

    // ── partes do arquivo ─────────────────────────────────────────────────

    private void appendHeader(StringBuilder sb, TableInfo table) {
        sb.append("package ").append(targetPackage).append(";\n\n");
        sb.append("/**\n");
        sb.append(" * Entidade gerada automaticamente para a tabela {@code ")
          .append(table.tableName()).append("}.\n");
        sb.append(" * Gerada pelo CRUD Generator — framework br.com.xyz.\n");
        sb.append(" */\n");
        sb.append("public class ").append(table.className()).append(" {\n");
    }

    private void appendConstructors(StringBuilder sb, TableInfo table, String className) {
        // Construtor vazio
        sb.append("\n    // ── Construtores ─────────────────────────────────────────────────────\n");
        sb.append("    public ").append(className).append("() {}\n\n");

        // Construtor completo
        sb.append("    public ").append(className).append("(");
        var cols = table.columns();
        for (int i = 0; i < cols.size(); i++) {
            sb.append(cols.get(i).javaType()).append(" ").append(cols.get(i).fieldName());
            if (i < cols.size() - 1) sb.append(", ");
        }
        sb.append(") {\n");
        for (var col : cols) {
            sb.append("        this.").append(col.fieldName())
              .append(" = ").append(col.fieldName()).append(";\n");
        }
        sb.append("    }\n");
    }

    private void appendGettersSetters(StringBuilder sb, TableInfo table) {
        sb.append("\n    // ── Getters e Setters ─────────────────────────────────────────────────\n");
        for (var col : table.columns()) {
            // getter
            sb.append("    public ").append(col.javaType()).append(" ")
              .append(col.getterName()).append("() { return ")
              .append(col.fieldName()).append("; }\n");
            // setter
            sb.append("    public void ").append(col.setterName())
              .append("(").append(col.javaType()).append(" v) { this.")
              .append(col.fieldName()).append(" = v; }\n");
        }
    }

    private void appendToString(StringBuilder sb, TableInfo table, String className) {
        sb.append("\n    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"").append(className).append("{\" +\n");
        var cols = table.columns();
        for (int i = 0; i < cols.size(); i++) {
            var col = cols.get(i);
            sb.append("               \"").append(col.fieldName())
              .append("=\" + ").append(col.fieldName());
            if (i < cols.size() - 1) sb.append(" + \", \" +\n");
        }
        sb.append(" + \"}\";\n");
        sb.append("    }\n");
    }
}
