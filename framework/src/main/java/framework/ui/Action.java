package framework.ui;

/**
 * Action – Uma operação nomeada associada a um {@link Runnable}.
 *
 * <p>Usada tanto em {@link Menu} (itens de menu) quanto em
 * {@link Form} (botões de ação como "Confirmar" e "Cancelar").
 */
public class Action {

    private final String   label;
    private final Runnable handler;

    public Action(String label, Runnable handler) {
        this.label   = label;
        this.handler = handler;
    }

    public String getLabel() { return label; }

    /** Executa o handler associado a esta ação. */
    public void execute() {
        if (handler != null) handler.run();
    }
}
