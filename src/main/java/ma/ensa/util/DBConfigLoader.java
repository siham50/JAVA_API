package ma.ensa.util;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class DBConfigLoader {
    private final Properties props = new Properties();
    private final String dbType;

    public DBConfigLoader() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) throw new RuntimeException("Le fichier db.properties est introuvable !");
            props.load(input);

            String dbTypeRaw = props.getProperty("db.type");
            if (dbTypeRaw == null) {
                throw new RuntimeException("La propriété 'db.type' est manquante dans db.properties.");
            }

            if (dbTypeRaw.contains("${dbType}")) {
                String systemDbType = System.getProperty("dbType");
                if (systemDbType == null) {
                    throw new RuntimeException("La propriété système 'dbType' n'a pas été définie (utilisez -DdbType=...).");
                }
                dbTypeRaw = dbTypeRaw.replace("${dbType}", systemDbType);
            }

            dbType = dbTypeRaw.trim();

        } catch (IOException e) {
            throw new RuntimeException("Erreur de chargement du fichier db.properties !", e);
        }
    }

    public String getUrl() {
        return props.getProperty(dbType + ".url").trim();
    }

    public String getUsername() {
        return props.getProperty(dbType + ".username").trim();
    }

    public String getPassword() {
        return props.getProperty(dbType + ".password").trim();
    }
}

