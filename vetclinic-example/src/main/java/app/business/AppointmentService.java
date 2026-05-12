package app.business;

import app.dao.AppointmentDao;
import app.dao.PetDao;
import app.dao.VetDao;
import app.domain.Appointment;
import framework.exceptions.NotFoundException;
import framework.exceptions.ValidationException;
import framework.util.Util;

import java.util.List;

/** Camada de negócio para consultas. */
public class AppointmentService {

    private final AppointmentDao apptDao = new AppointmentDao();
    private final PetDao         petDao  = new PetDao();
    private final VetDao         vetDao  = new VetDao();

    public List<Appointment> findAll()             { return apptDao.findAll(); }
    public List<Appointment> findByPet(int petId)  { return apptDao.findByPet(petId); }
    public List<Appointment> findByVet(int vetId)  { return apptDao.findByVet(vetId); }

    public Appointment findById(int id) {
        return apptDao.findById(id)
            .orElseThrow(() -> new NotFoundException("Consulta", id));
    }

    public int create(int petId, int vetId, String date, String time, String reason, String notes) {
        Util.requireNotEmpty(date,   "Data");
        Util.requireNotEmpty(reason, "Motivo");
        petDao.findById(petId)
            .orElseThrow(() -> new ValidationException("Animal com id=" + petId + " não encontrado."));
        vetDao.findById(vetId)
            .orElseThrow(() -> new ValidationException("Veterinário com id=" + vetId + " não encontrado."));

        var a = new Appointment();
        a.setPetId(petId);
        a.setVetId(vetId);
        a.setDate(date.trim());
        a.setTime(time == null ? "" : time.trim());
        a.setReason(reason.trim());
        a.setNotes(notes == null ? "" : notes.trim());
        return apptDao.insert(a);
    }

    public void update(int id, String date, String time, String reason, String notes) {
        var a = findById(id);
        if (date   != null && !date.isBlank())   a.setDate(date.trim());
        if (time   != null && !time.isBlank())   a.setTime(time.trim());
        if (reason != null && !reason.isBlank()) a.setReason(reason.trim());
        if (notes  != null && !notes.isBlank())  a.setNotes(notes.trim());
        apptDao.update(a);
    }

    public void delete(int id) {
        findById(id);
        apptDao.delete(id);
    }
}
