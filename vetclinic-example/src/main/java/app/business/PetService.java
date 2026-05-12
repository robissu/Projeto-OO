package app.business;

import app.dao.OwnerDao;
import app.dao.PetDao;
import app.domain.Pet;
import framework.exceptions.NotFoundException;
import framework.exceptions.ValidationException;
import framework.util.Util;

import java.util.List;

/** Camada de negócio para animais. */
public class PetService {

    private final PetDao   petDao   = new PetDao();
    private final OwnerDao ownerDao = new OwnerDao();

    public List<Pet> findAll()              { return petDao.findAll(); }
    public List<Pet> findByOwner(int id)    { return petDao.findByOwner(id); }

    public Pet findById(int id) {
        return petDao.findById(id)
            .orElseThrow(() -> new NotFoundException("Animal", id));
    }

    public int create(String name, String species, String breed, int age, int ownerId) {
        Util.requireNotEmpty(name, "Nome");
        Util.requireNotEmpty(species, "Espécie");
        Util.requirePositive(age, "Idade");
        ownerDao.findById(ownerId)
            .orElseThrow(() -> new ValidationException("Tutor com id=" + ownerId + " não encontrado."));

        var p = new Pet();
        p.setName(name.trim());
        p.setSpecies(species.trim());
        p.setBreed(breed == null ? "" : breed.trim());
        p.setAge(age);
        p.setOwnerId(ownerId);
        return petDao.insert(p);
    }

    public void update(int id, String name, String species, String breed, String ageStr) {
        var p = findById(id);
        if (name    != null && !name.isBlank())    p.setName(name.trim());
        if (species != null && !species.isBlank()) p.setSpecies(species.trim());
        if (breed   != null && !breed.isBlank())   p.setBreed(breed.trim());
        if (ageStr  != null && !ageStr.isBlank()) {
            try {
                int age = Integer.parseInt(ageStr.trim());
                Util.requirePositive(age, "Idade");
                p.setAge(age);
            } catch (NumberFormatException e) {
                throw new ValidationException("Idade inválida: " + ageStr);
            }
        }
        petDao.update(p);
    }

    public void delete(int id) {
        findById(id);
        petDao.delete(id);
    }
}
