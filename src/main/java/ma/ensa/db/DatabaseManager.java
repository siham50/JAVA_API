package ma.ensa.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseManager {
    Connection connect(String url, String username, String password) throws SQLException;
    List<Map<String, Object>> executeQuery(String query);
    int executeUpdate(String query);
    void close();
}
