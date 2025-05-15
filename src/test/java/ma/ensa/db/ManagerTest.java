package ma.ensa.db;

import ma.ensa.util.DBConfigLoader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManagerTest {

    private static DatabaseManager manager;
    private static DBConfigLoader config;

    @BeforeAll
    static void init() {
        config = new DBConfigLoader();
        switch (config.getDbType()) {
            case "mysql" -> manager = new MySQLManager();
            case "oracle" -> manager = new OracleManager();
            case "postgresql" -> manager = new PostgreSQLManager();
            case "sqlserver" -> manager = new SQLServerManager();
            default -> throw new RuntimeException("SGBDR non reconnu !");
        }
    }

    @Test
    @Order(1)
    void testConnection() {
        assertDoesNotThrow(() -> {
            Connection conn = manager.connect(
                    config.getUrl(), config.getUsername(), config.getPassword()
            );
            assertNotNull(conn);
        });
    }

    @Test
    @Order(2)
    void testSelect1() {
        List<Map<String, Object>> rows = manager.executeQuery("SELECT 1");
        assertFalse(rows.isEmpty());
    }

    @Test
    @Order(3)
    void testClose() {
        assertDoesNotThrow(manager::close);
    }



    //Sauf si la table est déjà pleine
    /*@Test
    @Order(4)
    void testSelectEmployesInitial() throws SQLException {
        manager.connect(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );

        List<Map<String, Object>> result = manager.executeQuery("SELECT * FROM employes");

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertFalse(result.isEmpty(), "La table 'employes' doit contenir des données !");
        assertTrue(result.get(0).containsKey("prenom"), "Chaque ligne doit contenir une colonne 'prenom'");
        assertTrue(result.get(0).containsKey("age"), "Chaque ligne doit contenir une colonne 'age'");
        assertTrue(result.get(0).containsKey("salaire"), "Chaque ligne doit contenir une colonne 'salaire'");
        assertTrue(result.get(0).containsKey("sexe"), "Chaque ligne doit contenir une colonne 'sexe'.");
        assertTrue(result.get(0).containsKey("email"), "Chaque ligne doit contenir une colonne 'email'.");

        manager.close();
    }*/


    @Test
    @Order(5)
    void testInsertEmployes() throws SQLException {
          manager.connect(config.getUrl(), config.getUsername(), config.getPassword());

    String query = """
        INSERT INTO employes (prenom, age, salaire, sexe, email) VALUES 
        ('A', 28, 39000, 'F', 'A@example.com'),
        ('B', 35, 47000, 'M', 'B@example.com'),
        ('C', 31, 42000, 'F', 'C@example.com'),
        ('D', 45, 55000, 'M', 'D@example.com'),
        ('E', 29, 41000, 'F', 'ESSS@example.com');
    """;

    int rows = manager.executeUpdate(query);
    assertEquals(5, rows, "Cinq lignes doivent être insérées dans la table 'employes'");

    manager.close();
}


    @Test
    @Order(6)
    void testSelectEmployesApresInsert() throws SQLException {
        manager.connect(config.getUrl(), config.getUsername(), config.getPassword());

        List<Map<String, Object>> result = manager.executeQuery("SELECT * FROM employes");

        assertNotNull(result, "Le résultat ne doit pas être nul");
        assertFalse(result.isEmpty(), "La table 'employes' doit contenir au moins une entrée");
        assertTrue(result.get(0).containsKey("prenom"), "Chaque ligne doit contenir une colonne 'prenom'.");
        assertTrue(result.get(0).containsKey("age"), "Chaque ligne doit contenir une colonne 'age'");
        assertTrue(result.get(0).containsKey("salaire"), "Chaque ligne doit contenir une colonne 'salaire'");
        assertTrue(result.get(0).containsKey("sexe"), "Chaque ligne doit contenir une colonne 'sexe'.");
        assertTrue(result.get(0).containsKey("email"), "Chaque ligne doit contenir une colonne 'email'.");
        manager.close();
    }


    @Test
    @Order(7)
    void testUpdateEmploye() throws SQLException {
        manager.connect(config.getUrl(), config.getUsername(), config.getPassword());

        String query = "UPDATE employes SET email = 'MMMM@example.com' WHERE prenom = 'C'";
        int rows = manager.executeUpdate(query);

        assertTrue(rows >= 1, "Au moins une ligne doit être mise à jour");

        manager.close();
    }


    @Test
    @Order(8)
    void testDeleteEmploye() throws SQLException {
        manager.connect(config.getUrl(), config.getUsername(), config.getPassword());

        String query = "DELETE FROM employes WHERE prenom = 'D'";
        int rows = manager.executeUpdate(query);

        assertTrue(rows >= 1, "Au moins une ligne doit être supprimée");

        manager.close();
    }


    @Test
    @Order(7)
    void testSelectEmployeAvecCondition() throws SQLException {
        manager.connect(config.getUrl(), config.getUsername(), config.getPassword());

        String query = "SELECT * FROM employes WHERE prenom = 'A'";
        List<Map<String, Object>> result = manager.executeQuery(query);

        assertNotNull(result, "Le résultat ne doit pas être nul");
        assertFalse(result.isEmpty(), "L'employé 'A' doit exister dans la base");
        assertEquals("A", result.get(0).get("prenom"), "Le prénom doit être 'A'");

        manager.close();
    }
}
