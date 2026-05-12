package generator.sample;

import framework.dao.DatabaseCreator;

import java.nio.file.Path;
import java.util.List;

/**
 * Cria uma base SQLite de exemplo para testar o gerador de CRUDs.
 *
 * <p>O esquema segue a aplicacao de exemplo do framework: tutores, veterinarios,
 * animais e consultas, incluindo chaves primarias e chaves estrangeiras.</p>
 */
public final class VetClinicSampleDatabase {

    public static final Path DEFAULT_PATH = Path.of("sample-data", "vetclinic.db");

    private VetClinicSampleDatabase() {
        // utility class
    }

    public static Path createDefault(boolean overwrite) {
        return create(DEFAULT_PATH, overwrite);
    }

    public static Path create(Path databasePath, boolean overwrite) {
        return DatabaseCreator.createSQLiteDatabase(databasePath, schemaAndSeed(), overwrite);
    }

    private static List<String> schemaAndSeed() {
        return List.of(
            """
            CREATE TABLE IF NOT EXISTS owners (
                id    INTEGER PRIMARY KEY AUTOINCREMENT,
                name  TEXT    NOT NULL,
                phone TEXT    DEFAULT '',
                email TEXT    DEFAULT ''
            )
            """,

            """
            CREATE TABLE IF NOT EXISTS vets (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                name      TEXT NOT NULL,
                specialty TEXT DEFAULT '',
                crmv      TEXT DEFAULT ''
            )
            """,

            """
            CREATE TABLE IF NOT EXISTS pets (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                name     TEXT    NOT NULL,
                species  TEXT    DEFAULT '',
                breed    TEXT    DEFAULT '',
                age      INTEGER DEFAULT 0,
                owner_id INTEGER NOT NULL,
                FOREIGN KEY (owner_id) REFERENCES owners(id)
            )
            """,

            """
            CREATE TABLE IF NOT EXISTS appointments (
                id      INTEGER PRIMARY KEY AUTOINCREMENT,
                pet_id  INTEGER NOT NULL,
                vet_id  INTEGER NOT NULL,
                date    TEXT    NOT NULL,
                time    TEXT    DEFAULT '',
                reason  TEXT    DEFAULT '',
                notes   TEXT    DEFAULT '',
                FOREIGN KEY (pet_id) REFERENCES pets(id),
                FOREIGN KEY (vet_id) REFERENCES vets(id)
            )
            """,

            "INSERT INTO owners (name, phone, email) VALUES ('Ana Souza', '55999990001', 'ana@email.com')",
            "INSERT INTO owners (name, phone, email) VALUES ('Bruno Lima', '55999990002', 'bruno@email.com')",
            "INSERT INTO vets (name, specialty, crmv) VALUES ('Dra. Carla Martins', 'Clinica geral', 'CRMV-1234')",
            "INSERT INTO vets (name, specialty, crmv) VALUES ('Dr. Diego Ramos', 'Dermatologia', 'CRMV-5678')",
            "INSERT INTO pets (name, species, breed, age, owner_id) VALUES ('Thor', 'Cachorro', 'Vira-lata', 4, 1)",
            "INSERT INTO pets (name, species, breed, age, owner_id) VALUES ('Mia', 'Gato', 'Siames', 2, 2)",
            "INSERT INTO appointments (pet_id, vet_id, date, time, reason, notes) VALUES (1, 1, '2026-05-20', '10:00', 'Check-up', 'Consulta de rotina')",
            "INSERT INTO appointments (pet_id, vet_id, date, time, reason, notes) VALUES (2, 2, '2026-05-21', '14:30', 'Coceira', 'Avaliar alergia')"
        );
    }
}
