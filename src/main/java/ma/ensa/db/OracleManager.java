package ma.ensa.db;

import java.sql.*;
import java.util.*;

public class OracleManager implements DatabaseManager {
    private Connection conn;

    @Override
    public Connection connect(String url, String username, String password) throws SQLException {
        conn = DriverManager.getConnection(url, username, password);
        System.out.println("Connexion Oracle établie avec succès !");
        return conn;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (conn == null) return results;

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Erreur requête SELECT Oracle : " + e.getMessage());
        }

        return results;
    }

    @Override
    public int executeUpdate(String query) {
        if (conn == null) return 0;

        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Erreur requête UPDATE Oracle : " + e.getMessage());
            return 0;
        }
    }

    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connexion Oracle fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur de fermeture Oracle : " + e.getMessage());
        }
    }
}
