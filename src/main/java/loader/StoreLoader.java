package loader;

import error.ErrorLogger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import parser.CSVParser;

public class StoreLoader {

    private final Connection connection;

    public StoreLoader(Connection connection) {
        this.connection = connection;
    }

    public void loadData(String filePath) {
        CSVParser parser = new CSVParser();

        try {
            List<String[]> rows = parser.readCSV(filePath, ',');

            int lineNumber = 2; // Zeile 1 ist Kopfzeile

            for (String[] row : rows) {
                try {
                    validateStore(row);

                    insertStore(row);

                    ErrorLogger.incrementLoaded();

                } catch (IllegalArgumentException e) {
                    ErrorLogger.logError(
                            filePath,
                            lineNumber,
                            "store",
                            String.join(",", row),
                            "Validierungsfehler",
                            e.getMessage()
                    );

                } catch (SQLException e) {
                    ErrorLogger.logError(
                            filePath,
                            lineNumber,
                            "store",
                            String.join(",", row),
                            "Datenbankfehler",
                            e.getMessage()
                    );
                }

                lineNumber++;
            }

        } catch (IOException e) {
            ErrorLogger.logError(
                    filePath,
                    0,
                    "store",
                    "",
                    "Dateifehler",
                    "Datei konnte nicht gelesen werden: " + e.getMessage()
            );
        }
    }

    private void validateStore(String[] row) {
        if (row.length < 3) {
            throw new IllegalArgumentException("Zu wenige Spalten für store. Erwartet: store_name, street, zip");
        }

        String storeName = row[0];

        if (storeName == null || storeName.isBlank()) {
            throw new IllegalArgumentException("store_name darf nicht NULL oder leer sein");
        }

        if (storeName.length() > 300) {
            throw new IllegalArgumentException("store_name ist länger als 300 Zeichen");
        }

        String street = row[1];
        if (street != null && street.length() > 200) {
            throw new IllegalArgumentException("street ist länger als 200 Zeichen");
        }

        String zip = row[2];
        if (zip != null && zip.length() > 10) {
            throw new IllegalArgumentException("zip ist länger als 10 Zeichen");
        }
    }

    private void insertStore(String[] row) throws SQLException {
        String sql = """
                INSERT INTO store (store_name, street, zip)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, emptyToNull(row[0]));
            statement.setString(2, emptyToNull(row[1]));
            statement.setString(3, emptyToNull(row[2]));

            statement.executeUpdate();
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}