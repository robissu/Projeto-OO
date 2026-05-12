package generator.writer;

import generator.model.ColumnInfo;
import generator.model.TableInfo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DaoWriter – Gera o código-fonte do DAO concreto que estende
 * {@code AbstractDao<Entidade, Integer>} do framework.
 *
 * <p>O DAO gerado implementa todos os 5 métodos de {@code IDao}
 * (insert, update, delete, findById, findAll) e adiciona métodos
 * {@code findByXxx} para cada chave estrangeira encontrada.
 *
 * <p>Usa lambdas {@code SqlBinder} / {@code SqlMapper} do framework
 * para código JDBC limpo e sem boilerplate.
 */
public class DaoWriter {

    private final String entityPackage;
    private final String daoPackage;

    public DaoWriter(String entityPackage, String daoPackage) {
        this.entityPackage = entityPackage;
        this.daoPackage    = daoPackage;
    }

    /** Gera o código-fonte completo do DAO para a tabela informada. */
    public String generate(TableInfo table) {
        var sb      = new StringBuilder();
        var entity  = table.className();
        var daoName = entity + "Dao";
        var varName = table.varName();
        var pk      = table.primaryKey();
        var pkType  = pk != null ? pk.boxedType() : "Integer";
        var pkCol   = pk != null ? pk.columnName() : "id";

        // ── Cabeçalho ────────────────────────────────────────────────────
        sb.append("package ").append(daoPackage).append(";\n\n");
        sb.append("import framework.dao.AbstractDao;\n");
        sb.append("import ").append(entityPackage).append(".").append(entity).append(";\n\n");
        sb.append("import java.sql.ResultSet;\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Optional;\n\n");
        sb.append("/**\n * DAO gerado automaticamente para {@code ")
          .append(table.tableName()).append("}.\n */\n");
        sb.append("public class ").append(daoName)
          .append(" extends AbstractDao<").append(entity).append(", ").append(pkType).append("> {\n\n");

        // ── Constante TABLE e SELECT ──────────────────────────────────────
        var colList = table.columns().stream()
            .map(ColumnInfo::columnName)
            .collect(Collectors.joining(", "));
        sb.append("    private static final String TABLE  = \"").append(table.tableName()).append("\";\n");
        sb.append("    private static final String SELECT = \"SELECT ").append(colList)
          .append(" FROM \" + TABLE;\n\n");

        // ── mapRow ────────────────────────────────────────────────────────
        sb.append("    private static ").append(entity)
          .append(" mapRow(ResultSet rs) throws java.sql.SQLException {\n");
        sb.append("        return new ").append(entity).append("(\n");
        var cols = table.columns();
        for (int i = 0; i < cols.size(); i++) {
            var col = cols.get(i);
            sb.append("            ").append(rsGetter(col))
              .append("(\"").append(col.columnName()).append("\")");
            sb.append(i < cols.size() - 1 ? ",\n" : "\n");
        }
        sb.append("        );\n    }\n\n");

        // ── INSERT ────────────────────────────────────────────────────────
        var insertable = table.insertableColumns();
        var colNames   = insertable.stream().map(ColumnInfo::columnName).collect(Collectors.joining(", "));
        var holders    = IntStream.range(0, insertable.size()).mapToObj(i -> "?").collect(Collectors.joining(", "));
        sb.append("    @Override\n");
        sb.append("    public ").append(pkType).append(" insert(").append(entity).append(" ").append(varName).append(") {\n");
        sb.append("        return executeUpdate(\n");
        sb.append("            \"INSERT INTO \" + TABLE + \"(").append(colNames)
          .append(") VALUES(").append(holders).append(")\",\n");
        sb.append("            ps -> {\n");
        for (int i = 0; i < insertable.size(); i++) {
            var col = insertable.get(i);
            sb.append("                ").append(psSetter(col)).append("(")
              .append(i + 1).append(", ").append(varName).append(".")
              .append(col.getterName()).append("());\n");
        }
        sb.append("            });\n    }\n\n");

        // ── UPDATE ────────────────────────────────────────────────────────
        var updatable = table.updatableColumns();
        if (!updatable.isEmpty()) {
            var setClauses = updatable.stream().map(c -> c.columnName() + "=?").collect(Collectors.joining(", "));
            sb.append("    @Override\n");
            sb.append("    public void update(").append(entity).append(" ").append(varName).append(") {\n");
            sb.append("        executeUpdate(\n");
            sb.append("            \"UPDATE \" + TABLE + \" SET ").append(setClauses)
              .append(" WHERE ").append(pkCol).append("=?\",\n");
            sb.append("            ps -> {\n");
            for (int i = 0; i < updatable.size(); i++) {
                var col = updatable.get(i);
                sb.append("                ").append(psSetter(col)).append("(")
                  .append(i + 1).append(", ").append(varName).append(".")
                  .append(col.getterName()).append("());\n");
            }
            sb.append("                ").append(pk != null ? psSetter(pk) : "ps.setObject")
              .append("(").append(updatable.size() + 1).append(", ")
              .append(varName).append(".").append(pk != null ? pk.getterName() + "()" : "getId()").append(");\n");
            sb.append("            });\n    }\n\n");
        } else {
            sb.append("    @Override\n");
            sb.append("    public void update(").append(entity).append(" ").append(varName)
              .append(") { /* sem colunas editáveis */ }\n\n");
        }

        // ── DELETE ────────────────────────────────────────────────────────
        sb.append("    @Override\n");
        sb.append("    public void delete(").append(pkType).append(" id) {\n");
        sb.append("        executeUpdate(\"DELETE FROM \" + TABLE + \" WHERE ")
          .append(pkCol).append("=?\", ps -> ps.setObject(1, id));\n");
        sb.append("    }\n\n");

        // ── FIND BY ID ────────────────────────────────────────────────────
        sb.append("    @Override\n");
        sb.append("    public Optional<").append(entity).append("> findById(").append(pkType).append(" id) {\n");
        sb.append("        return queryOne(SELECT + \" WHERE ").append(pkCol).append("=?\",\n");
        sb.append("            ps -> ps.setObject(1, id), ").append(daoName).append("::mapRow);\n");
        sb.append("    }\n\n");

        // ── FIND ALL ──────────────────────────────────────────────────────
        sb.append("    @Override\n");
        sb.append("    public List<").append(entity).append("> findAll() {\n");
        sb.append("        return query(SELECT, null, ").append(daoName).append("::mapRow);\n");
        sb.append("    }\n");

        // ── FIND BY FK (métodos extras) ───────────────────────────────────
        for (var col : table.columns()) {
            if (!col.isForeignKey()) continue;
            var methodName = "findBy" + ColumnInfo.toCamelCase(col.columnName(), true);
            sb.append("\n    /** Busca por FK: ").append(col.columnName())
              .append(" → ").append(col.fkTable()).append(".").append(col.fkColumn()).append(" */\n");
            sb.append("    public List<").append(entity).append("> ")
              .append(methodName).append("(").append(col.boxedType()).append(" fkId) {\n");
            sb.append("        return query(SELECT + \" WHERE ").append(col.columnName()).append("=?\",\n");
            sb.append("            ps -> ps.setObject(1, fkId), ").append(daoName).append("::mapRow);\n");
            sb.append("    }\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    // ── helpers de tipo ───────────────────────────────────────────────────

    private String rsGetter(ColumnInfo col) {
        return switch (col.javaType()) {
            case "int"     -> "rs.getInt";
            case "long"    -> "rs.getLong";
            case "double"  -> "rs.getDouble";
            case "boolean" -> "rs.getBoolean";
            case "byte[]"  -> "rs.getBytes";
            default        -> "rs.getString";
        };
    }

    private String psSetter(ColumnInfo col) {
        return switch (col.javaType()) {
            case "int"     -> "ps.setInt";
            case "long"    -> "ps.setLong";
            case "double"  -> "ps.setDouble";
            case "boolean" -> "ps.setBoolean";
            case "byte[]"  -> "ps.setBytes";
            default        -> "ps.setString";
        };
    }
}
