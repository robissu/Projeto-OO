package app.ui;

import app.business.VetService;
import app.domain.Vet;
import framework.ui.Action;
import framework.ui.Field;
import framework.ui.Form;
import framework.ui.Menu;
import framework.util.Util;

import java.util.List;

/** Interface console para gerenciamento de veterinários. */
public class VetUI {

    private final VetService service = new VetService();

    public void run() {
        var menu = new Menu("VETERINÁRIOS");
        menu.addItem(new Action("Listar todos", this::listAll));
        menu.addItem(new Action("Novo veterinário", this::add));
        menu.addItem(new Action("Editar veterinário", this::edit));
        menu.addItem(new Action("Remover veterinário", this::delete));
        menu.run();
    }

    private void printVets(List<Vet> vets) {
        if (vets.isEmpty()) {
            System.out.println("  Nenhum veterinário encontrado.");
            return;
        }
        System.out.println("  " +
            Util.col("ID", 5) +
            Util.col("Nome", 22) +
            Util.col("Especialidade", 18) +
            "CRMV");
        Util.printLine();
        for (var v : vets) {
            System.out.println("  " +
                Util.col(v.getId(), 5) +
                Util.col(v.getName(), 22) +
                Util.col(v.getSpecialty(), 18) +
                v.getCrmv());
        }
    }

    private void listAll() {
        Util.printHeader("LISTA DE VETERINÁRIOS");
        printVets(service.findAll());
        Util.pause();
    }

    private void add() {
        var form = new Form("NOVO VETERINÁRIO");
        form.addField(new Field("Nome", () -> Util.readString("  Nome:           ")));
        form.addField(new Field("Especialidade", () -> Util.readString("  Especialidade:  ")));
        form.addField(new Field("CRMV", () -> Util.readString("  CRMV:           ")));

        form.addAction(new Action("Confirmar", () -> {
            int id = service.create(
                form.fieldValue(0),
                form.fieldValue(1),
                form.fieldValue(2));
            System.out.println("\n  Veterinário salvo com id=" + id + ".");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void edit() {
        int id = Util.readInt("  ID do veterinário a editar: ");
        var vet = service.findById(id);

        var form = new Form("EDITAR VETERINÁRIO (id=" + id + ")");
        form.addField(new Field("Nome", () -> Util.readString("  Nome          [" + vet.getName() + "]: ")));
        form.addField(new Field("Especialidade", () -> Util.readString("  Especialidade [" + vet.getSpecialty() + "]: ")));
        form.addField(new Field("CRMV", () -> Util.readString("  CRMV          [" + vet.getCrmv() + "]: ")));

        form.addAction(new Action("Confirmar", () -> {
            service.update(id,
                form.fieldValue(0),
                form.fieldValue(1),
                form.fieldValue(2));
            System.out.println("\n  Veterinário atualizado.");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void delete() {
        int id = Util.readInt("  ID do veterinário a remover: ");
        var vet = service.findById(id);

        var form = new Form("REMOVER VETERINÁRIO — " + vet.getName());
        form.addAction(new Action("Confirmar exclusão", () -> {
            service.delete(id);
            System.out.println("\n  Veterinário removido.");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }
}
