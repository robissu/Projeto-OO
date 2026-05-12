package framework.ui;

import java.util.function.Supplier;

/**
 * Field – Representa um campo de entrada de dados em um formulário.
 *
 * <p>Cada campo possui um rótulo (label) e um leitor ({@link Supplier})
 * responsável por capturar e retornar o valor digitado pelo usuário.
 */
public class Field {

    private final String label;
    private final Supplier<String> reader;
    private String value = "";

    public Field(String label, Supplier<String> reader) {
        this.label  = label;
        this.reader = reader;
    }

    public String getLabel()  { return label; }
    public String getValue()  { return value; }
    public void   setValue(String v) { this.value = v; }

    /**
     * Invoca o leitor para capturar a entrada do usuário e armazena
     * o resultado internamente.
     *
     * @return o valor lido
     */
    public String read() {
        value = reader.get();
        return value;
    }
}
