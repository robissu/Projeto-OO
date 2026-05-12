package app.business;

import app.dao.OwnerDao;
import app.dao.PetDao;
import app.domain.Owner;
import framework.exceptions.NotFoundException;
import framework.exceptions.ValidationException;
import framework.util.Util;

import java.util.List;

/**
 * OwnerService — Camada de negócio para tutores.
 *
 * <p>Centraliza regras de validação e coordena o acesso ao {@link OwnerDao},
 * isolando a lógica de negócio da interface e da persistência.
 */
public class OwnerService {

    private final OwnerDao ownerDao = new OwnerDao();
    private final PetDao   petDao   = new PetDao();

    public List<Owner> findAll() {
        return ownerDao.findAll();
    }

    public Owner findById(int id) {
        return ownerDao.findById(id)
            .orElseThrow(() -> new NotFoundException("Tutor", id));
    }

    public int create(String name, String phone, String email) {
        Util.requireNotEmpty(name, "Nome");
        var o = new Owner();
        o.setName(name.trim());
        o.setPhone(phone == null ? "" : phone.trim());
        o.setEmail(email == null ? "" : email.trim());
        return ownerDao.insert(o);
    }

    public void update(int id, String name, String phone, String email) {
        var o = findById(id);
        if (name  != null && !name.isBlank())  o.setName(name.trim());
        if (phone != null && !phone.isBlank()) o.setPhone(phone.trim());
        if (email != null && !email.isBlank()) o.setEmail(email.trim());
        Util.requireNotEmpty(o.getName(), "Nome");
        ownerDao.update(o);
    }

    public void delete(int id) {
        findById(id); // garante que existe
        var pets = petDao.findByOwner(id);
        if (!pets.isEmpty()) {
            throw new ValidationException(
                "Tutor possui " + pets.size() + " animal(is) cadastrado(s). "
                + "Remova os animais antes de excluir o tutor.");
        }
        ownerDao.delete(id);
    }
}
