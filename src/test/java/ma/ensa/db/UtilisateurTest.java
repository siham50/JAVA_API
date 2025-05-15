package ma.ensa.db;

import ma.ensa.util.DBConfigLoader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

public class UtilisateurTest {

    private static DatabaseManager manager;
    private static DBConfigLoader config = new DBConfigLoader();

    static {
        switch (config.getDbType()) {
            case "mysql" -> manager = new MySQLManager();
            case "oracle" -> manager = new OracleManager();
            case "postgresql" -> manager = new PostgreSQLManager();
            case "sqlserver" -> manager = new SQLServerManager();
            default -> throw new RuntimeException("SGBDR non reconnu !");
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/utilisateurs.csv", numLinesToSkip = 1)

    void testInsertEmployeDepuisCSV(String prenom, int age, int salaire, String sexe, String email) throws SQLException {
        manager.connect(config.getUrl(), config.getUsername(), config.getPassword());

        String query = String.format(
                "INSERT INTO employes (prenom, age, salaire, sexe, email) VALUES ('%s', %d, %d, '%s', '%s')",
                prenom, age, salaire, sexe, email
        );

        int rows = manager.executeUpdate(query);

        assertEquals(1, rows, "Une ligne doit être insérée pour chaque employes");

        manager.close();
    }

}

