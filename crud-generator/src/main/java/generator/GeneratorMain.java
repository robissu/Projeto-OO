package generator;

import framework.exceptions.FrameworkException;
import generator.example.JavaCodeGeeksMetadataExample;
import generator.sample.VetClinicSampleDatabase;
import generator.ui.GeneratorUI;

import java.nio.file.Path;

/**
 * GeneratorMain - Ponto de entrada do Gerador de CRUDs.
 *
 * <p>Pode ser usado de quatro formas:</p>
 * <ul>
 *   <li>Interativo: java -jar crud-generator.jar</li>
 *   <li>Geracao: java -jar crud-generator.jar banco.db diretorio-saida</li>
 *   <li>Criar exemplo: java -jar crud-generator.jar --create-sample banco.db</li>
 *   <li>DatabaseMetaData: java -jar crud-generator.jar --metadata-example banco.db</li>
 * </ul>
 */
public class GeneratorMain {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                new GeneratorUI().run();
                return;
            }

            switch (args[0]) {
                case "--create-sample" -> {
                    var dbPath = args.length >= 2 ? Path.of(args[1]) : VetClinicSampleDatabase.DEFAULT_PATH;
                    var created = VetClinicSampleDatabase.create(dbPath, true);
                    System.out.println("Banco de exemplo criado em: " + created);
                }
                case "--metadata-example" -> {
                    var dbPath = args.length >= 2 ? args[1] : "";
                    JavaCodeGeeksMetadataExample.run(dbPath);
                }
                default -> {
                    var dbPath = args[0];
                    var outDir = args.length >= 2 ? args[1] : "output";
                    runBatch(dbPath, outDir);
                }
            }
        } catch (FrameworkException e) {
            System.err.println("\n[ERRO] " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n[ERRO INESPERADO] " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void runBatch(String dbPath, String outDir) {
        System.out.println("=".repeat(58));
        System.out.println("  GERADOR DE CRUDs - Modo batch");
        System.out.println("=".repeat(58));
        System.out.println("  Banco    : " + dbPath);
        System.out.println("  Saida    : " + outDir);
        System.out.println("=".repeat(58));

        var generator = new CrudGenerator(dbPath, outDir);
        var files = generator.generate();

        System.out.println("=".repeat(58));
        System.out.printf("  %d arquivo(s) gerado(s) com sucesso.%n", files.size());
        System.out.println("=".repeat(58));
    }
}
