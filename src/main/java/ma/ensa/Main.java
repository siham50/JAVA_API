package ma.ensa;

import java.sql.*;
import java.util.*;


import ma.ensa.db.*;
import ma.ensa.util.DBConfigLoader;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {

            DBConfigLoader config = new DBConfigLoader();

            System.out.println("SGBDR sélectionné : " + config.getDbType());
            System.out.println("URL de connexion : " + config.getUrl());
            System.out.println("Nom d'utilisateur : " + config.getUsername());
            System.out.println("Mot de passe : " + (config.getPassword().isEmpty() ? "(vide)" : "(fourni)"));

            DatabaseManager manager;
            switch (config.getDbType().toLowerCase()) {
                case "mysql" -> manager = new MySQLManager();
                case "oracle" -> manager = new OracleManager();
                case "postgresql" -> manager = new PostgreSQLManager();
                case "sqlserver" -> manager = new SQLServerManager();
                default -> throw new RuntimeException("SGBDR non trouvé : " + config.getDbType());
            }

            Connection conn = manager.connect(
                    config.getUrl(),
                    config.getUsername(),
                    config.getPassword()
            );


            // Requêtes SQL sur la table employes pour tester le .jar
            String insert = """
            INSERT INTO employes (prenom, age, salaire, sexe, email) VALUES
            ('RRR', 30, 40000, 'F', 'R@example.com'),
            ('YYY', 32, 45000, 'M', 'Y@example.com'),
            ('FFF', 28, 42000, 'F', 'F@example.com')
            """;

            String update = "UPDATE employes SET salaire = salaire + 1000 WHERE prenom = 'RRR'";

            String delete = "DELETE FROM employes WHERE prenom = 'FFF'";

            String select = "SELECT prenom, age, salaire, sexe, email FROM employes";

            int rowsInserted = manager.executeUpdate(insert);
            int rowsUpdated = manager.executeUpdate(update);

            List<Map<String, Object>> results = manager.executeQuery(select);
            System.out.println("Résultats de SELECT :");
            for (Map<String, Object> row : results) {
                System.out.println(row);
            }

            int rowsDeleted = manager.executeUpdate(delete);

            conn.close();
            manager.close();

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
        }
    }
}
