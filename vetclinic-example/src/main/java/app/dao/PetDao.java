package app.dao;

import app.domain.Pet;
import framework.dao.AbstractDao;

import java.util.List;
import java.util.Optional;

/** DAO concreto para a entidade {@link Pet}. */
public class PetDao extends AbstractDao<Pet, Integer> {

    private static final String SELECT =
        "SELECT id, name, species, breed, age, owner_id FROM pets";

    private static Pet map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Pet(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("species"),
            rs.getString("breed"),
            rs.getInt("age"),
            rs.getInt("owner_id")
        );
    }

    @Override
    public Integer insert(Pet p) {
        return executeUpdate(
            "INSERT INTO pets(name, species, breed, age, owner_id) VALUES(?, ?, ?, ?, ?)",
            ps -> {
                ps.setString(1, p.getName());
                ps.setString(2, p.getSpecies());
                ps.setString(3, p.getBreed());
                ps.setInt   (4, p.getAge());
                ps.setInt   (5, p.getOwnerId());
            });
    }

    @Override
    public void update(Pet p) {
        executeUpdate(
            "UPDATE pets SET name=?, species=?, breed=?, age=?, owner_id=? WHERE id=?",
            ps -> {
                ps.setString(1, p.getName());
                ps.setString(2, p.getSpecies());
                ps.setString(3, p.getBreed());
                ps.setInt   (4, p.getAge());
                ps.setInt   (5, p.getOwnerId());
                ps.setInt   (6, p.getId());
            });
    }

    @Override
    public void delete(Integer id) {
        executeUpdate("DELETE FROM pets WHERE id=?", ps -> ps.setInt(1, id));
    }

    @Override
    public Optional<Pet> findById(Integer id) {
        return queryOne(SELECT + " WHERE id=?", ps -> ps.setInt(1, id), PetDao::map);
    }

    @Override
    public List<Pet> findAll() {
        return query(SELECT + " ORDER BY name", null, PetDao::map);
    }

    /** Busca todos os animais de um tutor específico. */
    public List<Pet> findByOwner(int ownerId) {
        return query(SELECT + " WHERE owner_id=? ORDER BY name",
            ps -> ps.setInt(1, ownerId), PetDao::map);
    }
}
