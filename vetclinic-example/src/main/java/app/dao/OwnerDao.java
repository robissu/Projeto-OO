package app.dao;

import app.domain.Owner;
import framework.dao.AbstractDao;

import java.util.List;
import java.util.Optional;

/** DAO concreto para a entidade {@link Owner}. */
public class OwnerDao extends AbstractDao<Owner, Integer> {

    private static final String SELECT = "SELECT id, name, phone, email FROM owners";

    private static Owner map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Owner(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getString("email")
        );
    }

    @Override
    public Integer insert(Owner o) {
        return executeUpdate(
            "INSERT INTO owners(name, phone, email) VALUES(?, ?, ?)",
            ps -> {
                ps.setString(1, o.getName());
                ps.setString(2, o.getPhone());
                ps.setString(3, o.getEmail());
            });
    }

    @Override
    public void update(Owner o) {
        executeUpdate(
            "UPDATE owners SET name=?, phone=?, email=? WHERE id=?",
            ps -> {
                ps.setString(1, o.getName());
                ps.setString(2, o.getPhone());
                ps.setString(3, o.getEmail());
                ps.setInt   (4, o.getId());
            });
    }

    @Override
    public void delete(Integer id) {
        executeUpdate("DELETE FROM owners WHERE id=?",
            ps -> ps.setInt(1, id));
    }

    @Override
    public Optional<Owner> findById(Integer id) {
        return queryOne(SELECT + " WHERE id=?",
            ps -> ps.setInt(1, id),
            OwnerDao::map);
    }

    @Override
    public List<Owner> findAll() {
        return query(SELECT + " ORDER BY name", null, OwnerDao::map);
    }
}
