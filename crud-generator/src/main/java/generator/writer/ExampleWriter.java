package generator.writer;

import generator.model.ColumnInfo;
import generator.model.TableInfo;

import java.util.List;
import java.util.Map;

/**
 * ExampleWriter – Gera classe de exemplo que demonstra o uso do DAO
 * gerado, com dados aleatórios para cada tipo de campo.
 *
 * <p>A classe de exemplo gerada realiza o ciclo CRUD completo:
 * <ol>
 *   <li>Abre a conexão com o banco</li>
 *   <li>Insere um registro com dados aleatórios</li>
 *   <li>Lista todos os registros</li>
 *   <li>Busca o registro inserido por ID</li>
 *   <li>Atualiza o registro</li>
 *   <li>Remove o registro</li>
 * </ol>
 *
 * <p>Para colunas com chave estrangeira, o exemplo busca um ID existente
 * via {@code findAll()} no DAO da tabela referenciada antes de inserir.
 */
public class ExampleWriter {

    private final String entityPackage;
    private final String daoPackage;
    private final String exemploPackage;
    private final List<TableInfo> allTables;  // para resolver FKs

    public ExampleWriter(String entityPackage, String daoPackage,
                          String exemploPackage, List<TableInfo> allTables) {
        this.entityPackage  = entityPackage;
        this.daoPackage     = daoPackage;
        this.exemploPackage = exemploPackage;
        this.allTables      = allTables;
    }

    /** Gera o código-fonte completo da classe de exemplo. */
    public String generate(TableInfo table) {
        var sb         = new StringBuilder();
        var entity     = table.className();
        var daoName    = entity + "Dao";
        var varName    = table.varName();
        var pk         = table.primaryKey();
        var insertable = table.insertableColumns();

        // Resolve FKs: nome da FK → TableInfo da tabela referenciada
        var fkTables = resolveFkTables(table);

        // ── Cabeçalho ────────────────────────────────────────────────────
        sb.append("package ").append(exemploPackage).append(";\n\n");
        sb.append("import framework.dao.DBConnection;\n");
        sb.append("import ").append(entityPackage).append(".").append(entity).append(";\n");
        sb.append("import ").append(daoPackage).append(".").append(daoName).append(";\n");

        // imports dos DAOs de FK
        fkTables.forEach((col, fkTable) -> {
            var fkDaoName = fkTable.className() + "Dao";
            sb.append("import ").append(daoPackage).append(".").append(fkDaoName).append(";\n");
        });

        sb.append("\nimport java.util.Random;\n\n");
        sb.append("/**\n * Exemplo de uso gerado automaticamente para ")
          .append(entity).append(".\n");
        sb.append(" * Demonstra o ciclo CRUD completo com dados aleatórios.\n */\n");
        sb.append("public class ").append(entity).append("Exemplo {\n\n");
        sb.append("    private static final Random RNG = new Random();\n\n");
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        // 1. Abrir conexão — ajuste o caminho do banco conforme necessário\n");
        sb.append("        DBConnection.getInstance().open(\"jdbc:sqlite:banco.db\");\n\n");

        // ── Buscar IDs de FKs existentes ─────────────────────────────────
        if (!fkTables.isEmpty()) {
            sb.append("        // 2. Buscar IDs existentes para chaves estrangeiras\n");
            fkTables.forEach((col, fkTable) -> {
                var fkEntity  = fkTable.className();
                var fkDaoName = fkEntity + "Dao";
                var fkVarName = fkTable.varName();
                sb.append("        var ").append(fkVarName).append("Dao = new ").append(fkDaoName).append("();\n");
                sb.append("        var ").append(fkVarName).append("List = ").append(fkVarName).append("Dao.findAll();\n");
                sb.append("        if (").append(fkVarName).append("List.isEmpty()) {\n");
                sb.append("            System.out.println(\"[AVISO] Nenhum registro em '")
                  .append(fkTable.tableName()).append("' — insira dados antes de testar ")
                  .append(entity).append(".\");\n");
                sb.append("            return;\n        }\n");
                var fkPk = fkTable.primaryKey();
                var fkPkGetter = fkPk != null ? fkPk.getterName() + "()" : "getId()";
                sb.append("        var fk").append(ColumnInfo.toCamelCase(col.columnName(), true))
                  .append(" = ").append(fkVarName).append("List.get(0).").append(fkPkGetter).append(";\n");
            });
            sb.append("\n");
        }

        // ── Instanciar DAO ────────────────────────────────────────────────
        sb.append("        var dao = new ").append(daoName).append("();\n\n");

        // ── INSERT com dados aleatórios ───────────────────────────────────
        sb.append("        // 3. Criar entidade com dados aleatórios\n");
        sb.append("        var novo").append(entity).append(" = new ").append(entity).append("(\n");
        // construtor: PK primeiro, depois insertable
        for (int i = 0; i < table.columns().size(); i++) {
            var col = table.columns().get(i);
            if (col.isAutoIncrement()) {
                // passa 0 para auto-increment
                sb.append("            0");
            } else if (col.isForeignKey() && fkTables.containsKey(col)) {
                sb.append("            fk").append(ColumnInfo.toCamelCase(col.columnName(), true));
            } else {
                sb.append("            ").append(randomValue(col));
            }
            sb.append(i < table.columns().size() - 1 ? ",\n" : "\n");
        }
        sb.append("        );\n\n");

        sb.append("        int novoId = dao.insert(novo").append(entity).append(");\n");
        sb.append("        System.out.println(\"Inserido com id=\" + novoId);\n\n");

        // ── FIND ALL ──────────────────────────────────────────────────────
        sb.append("        // 4. Listar todos\n");
        sb.append("        System.out.println(\"\\n--- Todos os registros ---\");\n");
        sb.append("        dao.findAll().forEach(System.out::println);\n\n");

        // ── FIND BY ID ────────────────────────────────────────────────────
        sb.append("        // 5. Buscar por ID\n");
        sb.append("        var encontrado = dao.findById(novoId);\n");
        sb.append("        encontrado.ifPresentOrElse(\n");
        sb.append("            r -> System.out.println(\"\\nEncontrado: \" + r),\n");
        sb.append("            () -> System.out.println(\"\\nRegistro id=\" + novoId + \" não encontrado\")\n");
        sb.append("        );\n\n");

        // ── UPDATE ────────────────────────────────────────────────────────
        if (!table.updatableColumns().isEmpty()) {
            sb.append("        // 6. Atualizar\n");
            sb.append("        encontrado.ifPresent(r -> {\n");
            var firstUpdatable = table.updatableColumns().get(0);
            sb.append("            r.").append(firstUpdatable.setterName())
              .append("(").append(randomValue(firstUpdatable)).append(");\n");
            sb.append("            dao.update(r);\n");
            sb.append("            System.out.println(\"Atualizado: \" + r);\n");
            sb.append("        });\n\n");
        }

        // ── DELETE ────────────────────────────────────────────────────────
        sb.append("        // 7. Remover\n");
        sb.append("        dao.delete(novoId);\n");
        sb.append("        System.out.println(\"Registro id=\" + novoId + \" removido.\");\n\n");

        sb.append("        // 8. Confirmar remoção\n");
        sb.append("        System.out.println(\"Total restante: \" + dao.findAll().size());\n\n");

        sb.append("        DBConnection.getInstance().close();\n");
        sb.append("    }\n\n");

        // ── Métodos auxiliares de geração aleatória ───────────────────────
        appendRandomHelpers(sb);

        sb.append("}\n");
        return sb.toString();
    }

    // ── geração de valor aleatório por tipo ───────────────────────────────

    private String randomValue(ColumnInfo col) {
        return switch (col.javaType()) {
            case "int"     -> "RNG.nextInt(900) + 1";
            case "long"    -> "RNG.nextLong(900_000L) + 1L";
            case "double"  -> "Math.round(RNG.nextDouble() * 1000.0) / 10.0";
            case "boolean" -> "RNG.nextBoolean()";
            case "byte[]"  -> "new byte[]{(byte) RNG.nextInt(256)}";
            default        -> "randomString(8)";  // String
        };
    }

    private Map<ColumnInfo, TableInfo> resolveFkTables(TableInfo table) {
        var result = new java.util.LinkedHashMap<ColumnInfo, TableInfo>();
        for (var col : table.columns()) {
            if (!col.isForeignKey()) continue;
            allTables.stream()
                .filter(t -> t.tableName().equalsIgnoreCase(col.fkTable()))
                .findFirst()
                .ifPresent(fk -> result.put(col, fk));
        }
        return result;
    }

    private void appendRandomHelpers(StringBuilder sb) {
        sb.append("    // ── Geração de dados aleatórios ──────────────────────────────────────\n\n");
        sb.append("    private static final String CHARS = \"abcdefghijklmnopqrstuvwxyz\";\n\n");
        sb.append("    /** Gera uma String aleatória de comprimento {@code len}. */\n");
        sb.append("    private static String randomString(int len) {\n");
        sb.append("        var sb = new StringBuilder(len);\n");
        sb.append("        for (int i = 0; i < len; i++) {\n");
        sb.append("            sb.append(CHARS.charAt(RNG.nextInt(CHARS.length())));\n");
        sb.append("        }\n");
        sb.append("        return sb.toString();\n");
        sb.append("    }\n");
    }
}
