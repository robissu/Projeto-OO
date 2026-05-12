package app.dao;

import app.domain.Appointment;
import framework.dao.AbstractDao;

import java.util.List;
import java.util.Optional;

/** DAO concreto para a entidade {@link Appointment}. */
public class AppointmentDao extends AbstractDao<Appointment, Integer> {

    private static final String SELECT =
        "SELECT id, pet_id, vet_id, date, time, reason, notes FROM appointments";

    private static Appointment map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Appointment(
            rs.getInt("id"),
            rs.getInt("pet_id"),
            rs.getInt("vet_id"),
            rs.getString("date"),
            rs.getString("time"),
            rs.getString("reason"),
            rs.getString("notes")
        );
    }

    @Override
    public Integer insert(Appointment a) {
        return executeUpdate(
            "INSERT INTO appointments(pet_id, vet_id, date, time, reason, notes)"
            + " VALUES(?, ?, ?, ?, ?, ?)",
            ps -> {
                ps.setInt   (1, a.getPetId());
                ps.setInt   (2, a.getVetId());
                ps.setString(3, a.getDate());
                ps.setString(4, a.getTime());
                ps.setString(5, a.getReason());
                ps.setString(6, a.getNotes());
            });
    }

    @Override
    public void update(Appointment a) {
        executeUpdate(
            "UPDATE appointments SET pet_id=?, vet_id=?, date=?, time=?, reason=?, notes=?"
            + " WHERE id=?",
            ps -> {
                ps.setInt   (1, a.getPetId());
                ps.setInt   (2, a.getVetId());
                ps.setString(3, a.getDate());
                ps.setString(4, a.getTime());
                ps.setString(5, a.getReason());
                ps.setString(6, a.getNotes());
                ps.setInt   (7, a.getId());
            });
    }

    @Override
    public void delete(Integer id) {
        executeUpdate("DELETE FROM appointments WHERE id=?", ps -> ps.setInt(1, id));
    }

    @Override
    public Optional<Appointment> findById(Integer id) {
        return queryOne(SELECT + " WHERE id=?", ps -> ps.setInt(1, id), AppointmentDao::map);
    }

    @Override
    public List<Appointment> findAll() {
        return query(SELECT + " ORDER BY date, time", null, AppointmentDao::map);
    }

    /** Busca todas as consultas de um animal específico. */
    public List<Appointment> findByPet(int petId) {
        return query(SELECT + " WHERE pet_id=? ORDER BY date, time",
            ps -> ps.setInt(1, petId), AppointmentDao::map);
    }

    /** Busca todas as consultas de um veterinário específico. */
    public List<Appointment> findByVet(int vetId) {
        return query(SELECT + " WHERE vet_id=? ORDER BY date, time",
            ps -> ps.setInt(1, vetId), AppointmentDao::map);
    }
}
