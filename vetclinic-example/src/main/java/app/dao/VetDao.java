package app.dao;

import app.domain.Vet;
import framework.dao.AbstractDao;

import java.util.List;
import java.util.Optional;

/** DAO concreto para a entidade {@link Vet}. */
public class VetDao extends AbstractDao<Vet, Integer> {

    private static final String SELECT = "SELECT id, name, specialty, crmv FROM vets";

    private static Vet map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Vet(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("specialty"),
            rs.getString("crmv")
        );
    }

    @Override
    public Integer insert(Vet v) {
        return executeUpdate(
            "INSERT INTO vets(name, specialty, crmv) VALUES(?, ?, ?)",
            ps -> {
                ps.setString(1, v.getName());
                ps.setString(2, v.getSpecialty());
                ps.setString(3, v.getCrmv());
            });
    }

    @Override
    public void update(Vet v) {
        executeUpdate(
            "UPDATE vets SET name=?, specialty=?, crmv=? WHERE id=?",
            ps -> {
                ps.setString(1, v.getName());
                ps.setString(2, v.getSpecialty());
                ps.setString(3, v.getCrmv());
                ps.setInt   (4, v.getId());
            });
    }

    @Override
    public void delete(Integer id) {
        executeUpdate("DELETE FROM vets WHERE id=?", ps -> ps.setInt(1, id));
    }

    @Override
    public Optional<Vet> findById(Integer id) {
        return queryOne(SELECT + " WHERE id=?", ps -> ps.setInt(1, id), VetDao::map);
    }

    @Override
    public List<Vet> findAll() {
        return query(SELECT + " ORDER BY name", null, VetDao::map);
    }
}
