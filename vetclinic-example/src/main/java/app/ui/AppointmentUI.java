package app.ui;

import app.business.AppointmentService;
import app.business.PetService;
import app.business.VetService;
import app.domain.Appointment;
import framework.ui.Action;
import framework.ui.Field;
import framework.ui.Form;
import framework.ui.Menu;
import framework.util.Util;

import java.util.List;

/** Interface console para gerenciamento de consultas. */
public class AppointmentUI {

    private final AppointmentService service    = new AppointmentService();
    private final PetService         petService = new PetService();
    private final VetService         vetService = new VetService();

    public void run() {
        var menu = new Menu("CONSULTAS");
        menu.addItem(new Action("Listar todas as consultas",    this::listAll));
        menu.addItem(new Action("Agendar consulta",             this::add));
        menu.addItem(new Action("Editar consulta",              this::edit));
        menu.addItem(new Action("Cancelar consulta",            this::delete));
        menu.addItem(new Action("Consultas por animal",         this::byPet));
        menu.addItem(new Action("Consultas por veterinário",    this::byVet));
        menu.run();
    }

    // ── Helpers de exibição ───────────────────────────────────────────────

    private void printAppts(List<Appointment> appts) {
        if (appts.isEmpty()) {
            System.out.println("  Nenhuma consulta encontrada.");
            return;
        }
        System.out.println("  " +
            Util.col("ID",     5) +
            Util.col("Data",  12) +
            Util.col("Hora",   7) +
            Util.col("Animal", 9) +
            Util.col("Vet.",   6) +
            "Motivo");
        Util.printLine();
        for (var a : appts) {
            System.out.println("  " +
                Util.col(a.getId(),     5) +
                Util.col(a.getDate(),  12) +
                Util.col(a.getTime(),   7) +
                Util.col(a.getPetId(), 9) +
                Util.col(a.getVetId(), 6) +
                a.getReason());
        }
    }

    // ── Operações ─────────────────────────────────────────────────────────

    private void listAll() {
        Util.printHeader("LISTA DE CONSULTAS");
        printAppts(service.findAll());
        Util.pause();
    }

    private void byPet() {
        int petId = Util.readInt("  ID do animal: ");
        var pet   = petService.findById(petId);
        Util.printHeader("CONSULTAS DE " + pet.getName().toUpperCase());
        printAppts(service.findByPet(petId));
        Util.pause();
    }

    private void byVet() {
        int vetId = Util.readInt("  ID do veterinário: ");
        var vet   = vetService.findById(vetId);
        Util.printHeader("CONSULTAS DE Dr(a). " + vet.getName().toUpperCase());
        printAppts(service.findByVet(vetId));
        Util.pause();
    }

    private void add() {
        var form = new Form("AGENDAR CONSULTA");
        form.addField(new Field("AnimalID", () -> String.valueOf(Util.readInt("  ID do Animal:       "))));
        form.addField(new Field("VetID",    () -> String.valueOf(Util.readInt("  ID do Veterinário:  "))));
        form.addField(new Field("Data",     () -> Util.readString("  Data (AAAA-MM-DD):  ")));
        form.addField(new Field("Hora",     () -> Util.readString("  Hora (HH:MM):       ")));
        form.addField(new Field("Motivo",   () -> Util.readString("  Motivo:             ")));
        form.addField(new Field("Obs.",     () -> Util.readString("  Observações:        ")));

        form.addAction(new Action("Confirmar", () -> {
            int id = service.create(
                Integer.parseInt(form.fieldValue(0)),
                Integer.parseInt(form.fieldValue(1)),
                form.fieldValue(2),
                form.fieldValue(3),
                form.fieldValue(4),
                form.fieldValue(5));
            System.out.println("\n  Consulta agendada com id=" + id + ".");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void edit() {
        int id = Util.readInt("  ID da consulta a editar: ");
        var a  = service.findById(id);

        var form = new Form("EDITAR CONSULTA (id=" + id + ")");
        form.addField(new Field("Data",   () -> Util.readString("  Data   [" + a.getDate()   + "]: ")));
        form.addField(new Field("Hora",   () -> Util.readString("  Hora   [" + a.getTime()   + "]: ")));
        form.addField(new Field("Motivo", () -> Util.readString("  Motivo [" + a.getReason() + "]: ")));
        form.addField(new Field("Obs.",   () -> Util.readString("  Obs.   [" + a.getNotes()  + "]: ")));

        form.addAction(new Action("Confirmar", () -> {
            service.update(id,
                form.fieldValue(0),
                form.fieldValue(1),
                form.fieldValue(2),
                form.fieldValue(3));
            System.out.println("\n  Consulta atualizada.");
            Util.pause();
        }));
        form.addAction(new Action("Cancelar", () -> {}));
        form.render();
    }

    private void delete() {
        int id = Util.readInt("  ID da consulta a cancelar: ");
        var a  = service.findById(id);

        var form = new Form("CANCELAR CONSULTA id=" + id + " (" + a.getDate() + ")");
        form.addAction(new Action("Confirmar cancelamento", () -> {
            service.delete(id);
            System.out.println("\n  Consulta cancelada.");
            Util.pause();
        }));
        form.addAction(new Action("Voltar", () -> {}));
        form.render();
    }
}
