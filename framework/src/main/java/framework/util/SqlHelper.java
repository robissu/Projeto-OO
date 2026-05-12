package framework.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interfaces funcionais que permitem lambdas que lançam {@link SQLException},
 * eliminando blocos try/catch repetitivos nos DAOs.
 */
public final class SqlHelper {

    private SqlHelper() {}

    /** Consumer de PreparedStatement que pode lançar SQLException. */
    @FunctionalInterface
    public interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    /** Function de ResultSet → T que pode lançar SQLException. */
    @FunctionalInterface
    public interface SqlMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
