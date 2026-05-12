package app.business;

import app.dao.VetDao;
import app.domain.Vet;
import framework.exceptions.NotFoundException;
import framework.util.Util;

import java.util.List;

/** Camada de negócio para veterinários. */
public class VetService {

    private final VetDao vetDao = new VetDao();

    public List<Vet> findAll() { return vetDao.findAll(); }

    public Vet findById(int id) {
        return vetDao.findById(id)
            .orElseThrow(() -> new NotFoundException("Veterinário", id));
    }

    public int create(String name, String specialty, String crmv) {
        Util.requireNotEmpty(name,      "Nome");
        Util.requireNotEmpty(specialty, "Especialidade");
        Util.requireNotEmpty(crmv,      "CRMV");
        var v = new Vet();
        v.setName(name.trim());
        v.setSpecialty(specialty.trim());
        v.setCrmv(crmv.trim());
        return vetDao.insert(v);
    }

    public void update(int id, String name, String specialty, String crmv) {
        var v = findById(id);
        if (name      != null && !name.isBlank())      v.setName(name.trim());
        if (specialty != null && !specialty.isBlank()) v.setSpecialty(specialty.trim());
        if (crmv      != null && !crmv.isBlank())      v.setCrmv(crmv.trim());
        Util.requireNotEmpty(v.getName(), "Nome");
        vetDao.update(v);
    }

    public void delete(int id) {
        findById(id);
        vetDao.delete(id);
    }
}
