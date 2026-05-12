package framework.ui;

import framework.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu – Lista de {@link Action} exibida no console com loop de navegação.
 *
 * <p>O item [0] "Voltar / Sair" é adicionado automaticamente.
 * O menu continua exibindo até o usuário escolher a opção 0.
 *
 * <p>Exemplo de uso:
 * <pre>
 *   var menu = new Menu("Tutores");
 *   menu.addItem(new Action("Listar tutores", this::listAll));
 *   menu.addItem(new Action("Novo tutor",     this::add));
 *   menu.run();
 * </pre>
 */
public class Menu {

    private final String       title;
    private final String       exitLabel;
    private final List<Action> items = new ArrayList<>();

    public Menu(String title) {
        this(title, "Voltar / Sair");
    }

    public Menu(String title, String exitLabel) {
        this.title     = title;
        this.exitLabel = exitLabel;
    }

    public void addItem(Action action) {
        items.add(action);
    }

    /**
     * Exibe o menu em loop até o usuário escolher a opção de saída (0).
     */
    public void run() {
        while (true) {
            Util.printHeader(title);
            for (int i = 0; i < items.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, items.get(i).getLabel());
            }
            System.out.printf("  [0] %s%n", exitLabel);
            Util.printLine();

            int choice = Util.readIntInRange(
                    "  Opção: ", 0, items.size());

            if (choice == 0) break;

            try {
                items.get(choice - 1).execute();
            } catch (Exception ex) {
                System.out.println("\n  [ERRO] " + ex.getMessage());
                Util.pause();
            }
        }
    }
}
