package app;

import framework.dao.DBConnection;

/**
 * SchemaInitializer – Cria as tabelas do banco de dados na primeira execução.
 * Utiliza "CREATE TABLE IF NOT EXISTS" para ser idempotente.
 */
public class SchemaInitializer {

    public static void initialize() {
        var db = DBConnection.getInstance();

        db.execute("""
            CREATE TABLE IF NOT EXISTS owners (
                id    INTEGER PRIMARY KEY AUTOINCREMENT,
                name  TEXT    NOT NULL,
                phone TEXT    DEFAULT '',
                email TEXT    DEFAULT ''
            )
            """);

        db.execute("""
            CREATE TABLE IF NOT EXISTS vets (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                name      TEXT NOT NULL,
                specialty TEXT DEFAULT '',
                crmv      TEXT DEFAULT ''
            )
            """);

        db.execute("""
            CREATE TABLE IF NOT EXISTS pets (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                name     TEXT    NOT NULL,
                species  TEXT    DEFAULT '',
                breed    TEXT    DEFAULT '',
                age      INTEGER DEFAULT 0,
                owner_id INTEGER NOT NULL,
                FOREIGN KEY (owner_id) REFERENCES owners(id)
            )
            """);

        db.execute("""
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
            """);
    }
}
