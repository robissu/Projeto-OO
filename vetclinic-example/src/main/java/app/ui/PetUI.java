package app.ui;

import app.business.OwnerService;
import app.business.PetService;
import framework.ui.Action;
import framework.ui.Field;
import framework.ui.Form;
import framework.ui.Menu;
import framework.util.Util;

import java.util.List;
import app.domain.Pet;

/** Interface console para gerenciamento de animais. */
public class PetUI {

    private final PetService   service      = new PetService();
    private final OwnerService ownerService = new OwnerService();

    public void run() {
        var menu = new Menu("ANIMAIS");
        menu.addItem(new Action("Listar todos",           this::listAll));
        menu.addItem(new Action("Novo animal",            this::add));
        menu.addItem(new Action("Editar animal",          this::edit));
        menu.addItem(new Action("Remover animal",         this::delete));
        menu.addItem(new Action("Animais por tutor",      this::byOwner));
        menu.run();
    }

    // ── Helpers de exibição ───────────────────────────────────────────────

    private void printPets(List<Pet> pets) {
        if (pets.isEmpty()) {
            System.out.println("  Nenhum animal encontrado.");
            return;
        }
        System.out.println("  " +
            Util.col("ID",      5) +
            Util.col("Nome",   18) +
            Util.col("Espécie",10) +
            Util.col("Raça",   16) +
            Util.col("Idade",   7) +
            "Tutor ID");
        Util.printLine();
        for (var p : pets) {
            System.out.println("  " +
                Util.col(p.getId(),      5) +
                Util.col(p.getName(),   18) +
                Util.col(p.getSpecies(),10) +
                Util.col(p.getBreed(),  16) +
                Util.col(p.getAge(),     7) +
                p.getOwnerId());
        }
    }

    // ── Operações ─────────────────────────────────────────────────────────

    private void listAll() {
        Util.printHeader("LISTA DE ANIMAIS");
        printPets(service.findAll());
        Util.pause();
    }

    private void byOwner() {
        int oid    = Util.readInt("  ID do tutor: ");
        var owner  = ownerService.findById(oid);
        Util.printHeader("ANIMAIS DE " + owner.getName().toUpperCase());
        printPets(service.findByOwner(oid));
        Util.pause();
    }

    private void add() {
        var form = new Form("NOVO ANIMAL");
        form.addField(new Field("Nome",     () -> Util.readString("  Nome:     ")));
        form.addField(new Field("Espécie",  () -> Util.readString("  Espécie:  ")));
        form.addField(new Field("Raça",     () -> Util.readString("  Raça:     ")));
        form.addField(new Field("Idade",    () -> String.valueOf(Util.readInt("  Idade:    "))));
        form.addField(new Field("TutorID",  () -> String.valueOf(Util.readInt("  Tutor ID: "))));

        form.addAction(new Action("Confirmar", () -> {
            int id = service.create(
                form.fieldValue(0),
                form.fieldValue(1),
                form.fieldValue(2),
                Integer.parseInt(form.fieldValue(3)),
                Integer.parseInt(form.fieldValue(4)));
            System.out.println("\n  Animal salvo com id=" + id + ".");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void edit() {
        int id  = Util.readInt("  ID do animal a editar: ");
        var pet = service.findById(id);

        var form = new Form("EDITAR ANIMAL (id=" + id + ")");
        form.addField(new Field("Nome",    () -> Util.readString("  Nome    [" + pet.getName()    + "]: ")));
        form.addField(new Field("Espécie", () -> Util.readString("  Espécie [" + pet.getSpecies() + "]: ")));
        form.addField(new Field("Raça",    () -> Util.readString("  Raça    [" + pet.getBreed()   + "]: ")));
        form.addField(new Field("Idade",   () -> Util.readString("  Idade   [" + pet.getAge()     + "]: ")));

        form.addAction(new Action("Confirmar", () -> {
            service.update(id,
                form.fieldValue(0),
                form.fieldValue(1),
                form.fieldValue(2),
                form.fieldValue(3));
            System.out.println("\n  Animal atualizado.");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void delete() {
        int id  = Util.readInt("  ID do animal a remover: ");
        var pet = service.findById(id);

        var form = new Form("REMOVER ANIMAL — " + pet.getName());
        form.addAction(new Action("Confirmar exclusão", () -> {
            service.delete(id);
            System.out.println("\n  Animal removido.");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }
}
