package generator.writer;

import framework.exceptions.FrameworkException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * CodeFileWriter – Utilitário que persiste o código-fonte gerado em disco,
 * criando os diretórios necessários automaticamente.
 */
public class CodeFileWriter {

    private final Path outputRoot;

    /**
     * @param outputRoot diretório raiz onde os pacotes serão criados
     *                   (ex: {@code Path.of("output/src/main/java")})
     */
    public CodeFileWriter(Path outputRoot) {
        this.outputRoot = outputRoot;
    }

    /**
     * Grava um arquivo de código-fonte no pacote/diretório correto.
     *
     * @param packageName pacote Java (ex: "generated.domain")
     * @param fileName    nome do arquivo (ex: "Owner.java")
     * @param content     conteúdo do arquivo
     * @return caminho completo do arquivo gravado
     */
    public Path write(String packageName, String fileName, String content) {
        var packagePath = packageName.replace('.', '/');
        var dir         = outputRoot.resolve(packagePath);

        try {
            Files.createDirectories(dir);
            var filePath = dir.resolve(fileName);
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
            return filePath;
        } catch (IOException e) {
            throw new FrameworkException(
                "Erro ao gravar arquivo " + fileName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Retorna uma lista legível dos arquivos gerados com marcadores visuais.
     */
    public static String formatSummary(List<Path> paths, Path root) {
        var sb = new StringBuilder();
        paths.forEach(p -> sb.append("  ✓ ").append(root.relativize(p)).append("\n"));
        return sb.toString();
    }
}
