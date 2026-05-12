package app.ui;

import app.business.OwnerService;
import app.domain.Owner;
import framework.ui.Action;
import framework.ui.Field;
import framework.ui.Form;
import framework.ui.Menu;
import framework.util.Util;

import java.util.List;

/** Interface console para gerenciamento de tutores. */
public class OwnerUI {

    private final OwnerService service = new OwnerService();

    public void run() {
        var menu = new Menu("TUTORES");
        menu.addItem(new Action("Listar todos", this::listAll));
        menu.addItem(new Action("Novo tutor", this::add));
        menu.addItem(new Action("Editar tutor", this::edit));
        menu.addItem(new Action("Remover tutor", this::delete));
        menu.run();
    }

    private void printOwners(List<Owner> owners) {
        if (owners.isEmpty()) {
            System.out.println("  Nenhum tutor encontrado.");
            return;
        }
        System.out.println("  " +
            Util.col("ID", 5) +
            Util.col("Nome", 22) +
            Util.col("Telefone", 16) +
            "Email");
        Util.printLine();
        for (var o : owners) {
            System.out.println("  " +
                Util.col(o.getId(), 5) +
                Util.col(o.getName(), 22) +
                Util.col(o.getPhone(), 16) +
                o.getEmail());
        }
    }

    private void listAll() {
        Util.printHeader("LISTA DE TUTORES");
        printOwners(service.findAll());
        Util.pause();
    }

    private void add() {
        var form = new Form("NOVO TUTOR");
        form.addField(new Field("Nome", () -> Util.readString("  Nome:     ")));
        form.addField(new Field("Telefone", () -> Util.readString("  Telefone: ")));
        form.addField(new Field("Email", () -> Util.readString("  Email:    ")));

        form.addAction(new Action("Confirmar", () -> {
            int id = service.create(
                form.fieldValue(0),
                form.fieldValue(1),
                form.fieldValue(2));
            System.out.println("\n  Tutor salvo com id=" + id + ".");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void edit() {
        int id = Util.readInt("  ID do tutor a editar: ");
        var owner = service.findById(id);

        var form = new Form("EDITAR TUTOR (id=" + id + ")");
        form.addField(new Field("Nome", () -> Util.readString("  Nome     [" + owner.getName() + "]: ")));
        form.addField(new Field("Telefone", () -> Util.readString("  Telefone [" + owner.getPhone() + "]: ")));
        form.addField(new Field("Email", () -> Util.readString("  Email    [" + owner.getEmail() + "]: ")));

        form.addAction(new Action("Confirmar", () -> {
            service.update(id,
                form.fieldValue(0),
                form.fieldValue(1),
                form.fieldValue(2));
            System.out.println("\n  Tutor atualizado.");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void delete() {
        int id = Util.readInt("  ID do tutor a remover: ");
        var owner = service.findById(id);

        var form = new Form("REMOVER TUTOR — " + owner.getName());
        form.addAction(new Action("Confirmar exclusão", () -> {
            service.delete(id);
            System.out.println("\n  Tutor removido.");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }
}
