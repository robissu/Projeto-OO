package framework.ui;

import framework.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Form – Janela/formulário para o console.
 *
 * <p>Um formulário possui:
 * <ul>
 *   <li>Um <b>título</b> exibido no cabeçalho</li>
 *   <li>Uma lista de <b>campos</b> ({@link Field}) lidos em sequência</li>
 *   <li>Uma lista de <b>ações</b> ({@link Action}) apresentadas ao final</li>
 * </ul>
 *
 * <p>Ao chamar {@link #render()}, o formulário:
 * <ol>
 *   <li>Exibe o cabeçalho com o título</li>
 *   <li>Lê cada campo em ordem</li>
 *   <li>Apresenta as ações numeradas e executa a escolhida</li>
 * </ol>
 *
 * <p>Exemplo de uso:
 * <pre>
 *   var form = new Form("Novo Tutor");
 *   form.addField(new Field("Nome",  () -&gt; Util.readString("  Nome:  ")));
 *   form.addField(new Field("Email", () -&gt; Util.readString("  Email: ")));
 *   form.addAction(new Action("Confirmar", () -&gt; { ... }));
 *   form.addAction(new Action("Cancelar",  () -&gt; {}));
 *   form.render();
 * </pre>
 */
public class Form {

    private final String       title;
    private final List<Field>  fields  = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();

    public Form(String title) {
        this.title = title;
    }

    public void addField(Field field)   { fields.add(field);   }
    public void addAction(Action action){ actions.add(action); }

    /** Retorna o valor do campo pela posição (índice 0-based). */
    public String fieldValue(int index) {
        return fields.get(index).getValue();
    }

    /** Pré-preenche o valor de um campo (útil em formulários de edição). */
    public void setFieldValue(int index, String value) {
        fields.get(index).setValue(value);
    }

    public String getTitle() { return title; }

    /**
     * Renderiza o formulário no console: exibe o cabeçalho, lê os campos
     * e executa a ação escolhida pelo usuário.
     */
    public void render() {
        Util.printHeader(title);

        // Lê todos os campos em sequência
        for (var field : fields) {
            field.read();
        }

        if (actions.isEmpty()) return;

        System.out.println();
        Util.printLine();
        for (int i = 0; i < actions.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, actions.get(i).getLabel());
        }

        int choice = Util.readIntInRange(
                "  Escolha uma ação (1-" + actions.size() + "): ",
                1, actions.size());

        try {
            actions.get(choice - 1).execute();
        } catch (Exception ex) {
            System.out.println("\n  [ERRO] " + ex.getMessage());
            Util.pause();
        }
    }
}
