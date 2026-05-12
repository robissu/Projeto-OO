package framework.util;

import framework.exceptions.ValidationException;

import java.io.Console;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Util – Classe utilitária estática com métodos para:
 * <ul>
 *   <li>Leitura de dados via teclado (console)</li>
 *   <li>Conversões de tipos</li>
 *   <li>Validações de regras de entrada</li>
 *   <li>Formatação do console</li>
 * </ul>
 */
public final class Util {

    private static final Console CONSOLE = System.console();
    private static final Scanner SCANNER = new Scanner(System.in, consoleCharset());
    private static final int LINE_WIDTH = 58;

    private Util() { /* classe utilitária — não instanciar */ }

    // ──────────────────────────────────────────────────────────────────────
    // Leitura de dados
    // ──────────────────────────────────────────────────────────────────────

    private static Charset consoleCharset() {
        var console = System.console();
        return console != null ? console.charset() : Charset.defaultCharset();
    }

    private static String readLine(String prompt) {
        if (CONSOLE != null) {
            var line = CONSOLE.readLine("%s", prompt);
            return line == null ? "" : line.trim();
        }

        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /** Lê uma linha de texto. */
    public static String readString(String prompt) {
        return readLine(prompt);
    }

    /** Lê um inteiro, repetindo o prompt até que a entrada seja válida. */
    public static int readInt(String prompt) {
        while (true) {
            var line = readLine(prompt);
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Número inteiro inválido. Tente novamente.");
            }
        }
    }

    /** Lê um double, repetindo o prompt até que a entrada seja válida. */
    public static double readDouble(String prompt) {
        while (true) {
            var line = readLine(prompt);
            try {
                return Double.parseDouble(line.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("  [!] Número inválido. Tente novamente.");
            }
        }
    }

    /** Lê um inteiro dentro do intervalo [min, max]. */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int v = readInt(prompt);
            if (v >= min && v <= max) return v;
            System.out.printf("  [!] Digite um valor entre %d e %d.%n", min, max);
        }
    }

    /** Aguarda o usuário pressionar ENTER. */
    public static void pause() {
        readLine("\n  Pressione ENTER para continuar...");
    }

    // ──────────────────────────────────────────────────────────────────────
    // Validações
    // ──────────────────────────────────────────────────────────────────────

    /** Lança ValidationException se o valor for nulo ou vazio. */
    public static void requireNotEmpty(String value, String fieldName) {
        if (value == null || value.isBlank())
            throw new ValidationException(fieldName + " não pode ser vazio.");
    }

    /** Lança ValidationException se o valor não for positivo. */
    public static void requirePositive(int value, String fieldName) {
        if (value <= 0)
            throw new ValidationException(fieldName + " deve ser positivo.");
    }

    /** Lança ValidationException se o valor não for positivo. */
    public static void requirePositive(double value, String fieldName) {
        if (value <= 0)
            throw new ValidationException(fieldName + " deve ser positivo.");
    }

    /** Lança ValidationException se o valor estiver fora do intervalo. */
    public static void requireRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max)
            throw new ValidationException(
                    fieldName + " deve estar entre " + min + " e " + max + ".");
    }

    // ──────────────────────────────────────────────────────────────────────
    // Formatação do console
    // ──────────────────────────────────────────────────────────────────────

    /** Imprime uma linha de separação. */
    public static void printLine() {
        printLine('-');
    }

    /** Imprime uma linha de separação com o caractere indicado. */
    public static void printLine(char ch) {
        System.out.println("  " + String.valueOf(ch).repeat(LINE_WIDTH));
    }

    /** Imprime um cabeçalho com título centralizado. */
    public static void printHeader(String title) {
        System.out.println();
        printLine('=');
        System.out.println("  " + title);
        printLine('=');
    }

    /** Formata uma coluna com largura fixa (trunca se necessário). */
    public static String col(Object value, int width) {
        var s = value == null ? "" : value.toString();
        if (s.length() > width) s = s.substring(0, width - 1) + "…";
        return String.format("%-" + width + "s", s);
    }
}
