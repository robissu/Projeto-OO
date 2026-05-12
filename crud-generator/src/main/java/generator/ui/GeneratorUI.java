package generator.ui;

import framework.ui.Action;
import framework.ui.Field;
import framework.ui.Form;
import framework.ui.Menu;
import framework.util.Util;
import generator.CrudGenerator;
import generator.example.JavaCodeGeeksMetadataExample;
import generator.model.TableInfo;
import generator.sample.VetClinicSampleDatabase;

import java.nio.file.Path;
import java.util.List;

/**
 * GeneratorUI - Interface console do gerador de CRUDs.
 *
 * <p>Usa as classes Menu e Form do framework para apresentar as opcoes ao
 * usuario de forma consistente.</p>
 */
public class GeneratorUI {

    public void run() {
        var menu = new Menu("GERADOR DE CRUDs - Framework XYZ", "Sair");

        menu.addItem(new Action("Gerar CRUDs a partir de qualquer banco SQLite/JDBC", this::gerarCruds));
        menu.addItem(new Action("Pre-visualizar tabelas de qualquer banco", this::previsualizar));
        menu.addItem(new Action("Criar banco SQLite de exemplo", this::criarBancoExemplo));
        menu.addItem(new Action("Executar exemplo DatabaseMetaData estilo JavaCodeGeeks", this::executarExemploJavaCodeGeeks));
        menu.addItem(new Action("Sobre o gerador", this::sobre));

        menu.run();
    }

    // -- Criacao de banco de exemplo ---------------------------------------

    private void criarBancoExemplo() {
        Util.printHeader("CRIAR BANCO DE EXEMPLO");
        System.out.println("  Esta opcao cria uma base SQLite com owners, vets, pets e appointments.");
        System.out.println("  Ela mantem a funcionalidade do projeto original: abrir/criar banco");
        System.out.println("  SQLite e executar os CREATE TABLE usando o framework.");
        System.out.println();

        var pathText = Util.readString(
            "  Caminho do arquivo .db (ENTER para ./sample-data/vetclinic.db):\n  > ");
        var overwriteText = Util.readString(
            "  Sobrescrever se ja existir? (s/N):\n  > ");

        var dbPath = pathText.isBlank()
            ? VetClinicSampleDatabase.DEFAULT_PATH
            : Path.of(pathText.trim());
        var overwrite = overwriteText.equalsIgnoreCase("s") || overwriteText.equalsIgnoreCase("sim");

        var created = VetClinicSampleDatabase.create(dbPath, overwrite);

        System.out.println();
        System.out.println("  Banco criado/atualizado em:");
        System.out.println("  " + created);
        System.out.println();
        System.out.println("  Agora voce pode usar a opcao 2 para pre-visualizar esse banco");
        System.out.println("  ou a opcao 1 para gerar os CRUDs.");
        Util.pause();
    }

    // -- Exemplo DatabaseMetaData ------------------------------------------

    private void executarExemploJavaCodeGeeks() {
        Util.printHeader("EXEMPLO JDBC DatabaseMetaData");
        System.out.println("  Exemplo didatico inspirado no artigo JavaCodeGeeks.");
        System.out.println("  Ele mostra getMetaData(), getTables() e getColumns().");
        System.out.println();

        var dbPath = Util.readString(
            "  Caminho do banco ou URL JDBC (ENTER para criar/usar ./sample-data/vetclinic.db):\n  > ");

        JavaCodeGeeksMetadataExample.run(dbPath);
        Util.pause();
    }

    // -- Geracao completa --------------------------------------------------

    private void gerarCruds() {
        var form = new Form("GERAR CRUDs");
        form.addField(new Field("Banco", () -> Util.readString(
            "  Caminho do banco ou URL JDBC:\n" +
            "  Exemplos:\n" +
            "    C:\\crud-test\\banco.db\n" +
            "    ./sample-data/vetclinic.db\n" +
            "    jdbc:sqlite:C:/crud-test/banco.db\n" +
            "  > ")));
        form.addField(new Field("Saida", () -> Util.readString(
            "  Diretorio de saida (ENTER para ./output):\n  > ")));

        form.addAction(new Action("Confirmar e gerar", () -> {
            var dbPath = form.fieldValue(0).trim();
            var outDir = form.fieldValue(1).trim();
            if (outDir.isEmpty()) outDir = "output";

            Util.requireNotEmpty(dbPath, "Caminho do banco");

            var generator = new CrudGenerator(dbPath, outDir);
            var files = generator.generate();

            Util.printHeader("GERACAO CONCLUIDA");
            System.out.printf("  %d arquivo(s) gerado(s) em: %s%n%n",
                files.size(), Path.of(outDir).toAbsolutePath());

            printSummaryTree(files, Path.of(outDir));
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    // -- Pre-visualizacao --------------------------------------------------

    private void previsualizar() {
        var dbPath = Util.readString(
            "\n  Caminho do banco ou URL JDBC:\n" +
            "  Exemplos:\n" +
            "    C:\\crud-test\\banco.db\n" +
            "    ./sample-data/vetclinic.db\n" +
            "    jdbc:sqlite:C:/crud-test/banco.db\n" +
            "  > ").trim();

        if (dbPath.isEmpty()) {
            System.out.println("  [!] Caminho nao informado.");
            Util.pause();
            return;
        }

        Util.printHeader("TABELAS ENCONTRADAS");
        var generator = new CrudGenerator(dbPath, "output");
        List<TableInfo> tables;

        try {
            tables = generator.preview();
        } catch (Exception e) {
            System.out.println("  [ERRO] " + e.getMessage());
            Util.pause();
            return;
        }

        printTables(tables);
        Util.pause();
    }

    private void printTables(List<TableInfo> tables) {
        if (tables.isEmpty()) {
            System.out.println("  Nenhuma tabela encontrada.");
            return;
        }

        for (var table : tables) {
            System.out.println();
            System.out.println("   Tabela: " + table.tableName()
                + "  ->  classe: " + table.className());

            System.out.println("      " + Util.col("Coluna", 22)
                + Util.col("Tipo SQL", 14) + Util.col("Java", 10)
                + Util.col("PK", 5) + Util.col("FK", 5) + "Nullable");
            System.out.println("      " + "-".repeat(64));

            for (var col : table.columns()) {
                var pkFlag = col.isPrimaryKey() ? "X" : "";
                var fkFlag = col.isForeignKey()
                    ? "-> " + col.fkTable() + "." + col.fkColumn()
                    : "";
                System.out.println("      "
                    + Util.col(col.columnName(), 22)
                    + Util.col(col.jdbcTypeName(), 14)
                    + Util.col(col.javaType(), 10)
                    + Util.col(pkFlag, 5)
                    + (fkFlag.isEmpty()
                        ? Util.col("", 5) + (col.isNullable() ? "sim" : "nao")
                        : fkFlag));
            }
            System.out.println("  -> " + table.columns().size() + " coluna(s)");
        }

        System.out.println();
        System.out.println("  Total: " + tables.size() + " tabela(s)");
        System.out.println("  Arquivos que seriam gerados: "
            + (tables.size() * 3) + " (Entidade + DAO + Exemplo por tabela)");
    }

    // -- Sobre -------------------------------------------------------------

    private void sobre() {
        Util.printHeader("SOBRE O GERADOR DE CRUDs");
        System.out.println("""
              Estrutura do projeto:

              1. framework
                 Modulo reutilizavel com DBConnection, AbstractDao, excecoes,
                 utilitarios, Menu/Form e DatabaseCreator.

              2. crud-generator
                 Modulo do gerador. Ele depende do framework via Maven e nao
                 contem copia do framework.

              3. vetclinic-example
                 Aplicacao de exemplo do framework. Ela preserva a funcionalidade
                 original de criar uma base SQLite e usar DAOs sobre ela.

              Entrada base do gerador:
              - MetadataInputSource             : abstracao de entrada
              - JdbcDatabaseMetadataInputSource : entrada padrao por JDBC
              - DatabaseMetaDataReader          : leitura dos metadados
              - getTables()                     : lista tabelas
              - getColumns()                    : lista colunas, tipos e nullability
              - getPrimaryKeys()                : identifica PKs
              - getImportedKeys()               : identifica FKs

              Saidas geradas:
              - generated.domain  : entidades
              - generated.dao     : DAOs
              - generated.exemplo : exemplos de uso

              Opcoes uteis:
              - Use a opcao 3 para criar um banco de exemplo.
              - Use a opcao 4 para executar o exemplo didatico DatabaseMetaData.
              - Use as opcoes 1 e 2 para ler qualquer outro .db ou URL JDBC.
            """);
        Util.pause();
    }

    // -- Helpers de apresentacao ------------------------------------------

    private void printSummaryTree(List<Path> files, Path root) {
        String lastPkg = "";
        for (var f : files) {
            var rel = root.toAbsolutePath().relativize(f.toAbsolutePath()).toString();
            var parts = rel.split("[/\\\\]");
            var pkg = String.join("/", java.util.Arrays.copyOf(parts, parts.length - 1));

            if (!pkg.equals(lastPkg)) {
                System.out.println("  [DIR] " + pkg + "/");
                lastPkg = pkg;
            }
            System.out.println("     - " + parts[parts.length - 1]);
        }
    }
}
