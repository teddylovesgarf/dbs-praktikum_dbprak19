package loader;

import error.ErrorLogger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import parser.CSVParser;
/*ReviewLoader lädt Rezensionen aus der CSV-Datei. Zuerst wird jede Zeile validiert: 
Spaltenanzahl, Pflichtfelder, Rating als Zahl zwischen 1 und 5, Helpful als Zahl und 
Datum im Format yyyy-MM-dd. Danach wird geprüft, ob das Produkt existiert. 
Der Kunde wird anhand des Usernamens gesucht oder neu angelegt. 
Anschließend wird die Rezension mit einem PreparedStatement in die Tabelle review eingefügt. 
Fehlerhafte Datensätze werden über den ErrorLogger mit Datei, Zeile, Entität, Fehlerart und Begründung protokolliert. 
Besonders wichtig ist die relationenübergreifende Prüfung, dass eine Rezension nur für ein existierendes Produkt 
geladen werden darf. */

// * Die Klasse ReviewLoader lädt Rezensionen aus einer CSV Datei in die Datenbank.
/*Erwartetes CSV Format:
 product, rating, helpful, reviewdate, user, summary, content
 Der Benutzer (user) aus der CSV wird in der Tabelle customer gesucht oder neu angelegt.
  Danach wird die Rezension dem passenden Produkt und Kunden zugeordnet.
*/
public class ReviewLoader {

    private final Connection connection;
    //Erstellt einen ReviewLoader mit einer bestehenden Datenbankverbindung.
    public ReviewLoader(Connection connection) {
        this.connection = connection;
    }
     /**
     * Liest die CSV Datei ein und verarbeitet jede Rezension zeilenweise.
     * Gültige Zeilen werden in die Datenbank geschrieben.
     * Fehlerhafte Zeilen werden protokolliert.
     *
     * @param filePath Pfad zur CSV Datei
     */
    public void loadData(String filePath) {
        CSVParser parser = new CSVParser();

        try {
            List<String[]> rows = parser.readCSV(filePath, ',');

            int lineNumber = 2; // Zeile 1 ist die Kopfzeile

            for (String[] row : rows) {
                try {
                    validateReview(row);

                    insertReview(row);

                    ErrorLogger.incrementLoaded();

                } catch (IllegalArgumentException e) {
                    ErrorLogger.logError(
                            filePath, lineNumber, "review", "unbekannt",
    String.join(",", row), "Validierungsfehler", e.getMessage()
);

                } catch (SQLException e) {
                    ErrorLogger.logError(filePath, lineNumber, "review", "unbekannt",
    String.join(",", row), "Datenbankfehler", e.getMessage()
);
                }

                lineNumber++;
            }

        } catch (IOException e) {
            ErrorLogger.logError(filePath, 0, "review", "file",
    "", "Dateifehler", "Datei konnte nicht gelesen werden: " + e.getMessage()
            );
        }
    }

    // Validiert die Daten einer Rezension. Überprüft die Anzahl der Spalten, die Gültigkeit von product_id, rating, reviewdate und user.
    private void validateReview(String[] row) {
        if (row.length < 7) {
            throw new IllegalArgumentException(
                    "Zu wenige Spalten für review. Erwartet: product, rating, helpful, reviewdate, user, summary, content"
            );
        }

        String productId = row[0];
        String ratingRaw = row[1];
        String helpfulRaw = row[2];
        String reviewDateRaw = row[3];
        String userName = row[4];

        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("product_id fehlt");
        }

        if (ratingRaw == null || ratingRaw.isBlank()) {
            throw new IllegalArgumentException("rating fehlt");
        }

        int rating;

        try {
            rating = Integer.parseInt(ratingRaw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("rating ist keine ganze Zahl");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating muss zwischen 1 und 5 liegen");
        }
        if (helpfulRaw != null && !helpfulRaw.isBlank()) {
    try {
        int helpful = Integer.parseInt(helpfulRaw.trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("helpful ist keine ganze Zahl");
    }
}

        if (reviewDateRaw == null || reviewDateRaw.isBlank()) {
            throw new IllegalArgumentException("reviewdate fehlt");
        }

        try {
            LocalDate.parse(reviewDateRaw.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("reviewdate hat kein gültiges Format yyyy-MM-dd");
        }

        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("user fehlt");
        }
    }

    /*Fügt eine Rezesion in die Datenbank ein. 
    Falls keine Kunde gefunden wird, wird eine neue angelegt 
    @param row sind geprüfte CSV Zeilen    
    */
    private void insertReview(String[] row) throws SQLException {
        String productId = row[0].trim();
        int rating = Integer.parseInt(row[1].trim());
        LocalDate reviewDate = LocalDate.parse(row[3].trim());
        String userName = row[4].trim();

        String summary = emptyToNull(row[5]);
        String content = emptyToNull(row[6]);

        String reviewText = buildReviewText(summary, content);

        if (!productExists(productId)) {
            throw new IllegalArgumentException("Produkt mit product_id " + productId + " existiert nicht");
        }

        int customerId = findOrCreateCustomer(userName);

        String sql = """
                INSERT INTO review (customer_id, product_id, rating, review_text, review_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setString(2, productId);
            statement.setInt(3, rating);
            statement.setString(4, reviewText);
            statement.setDate(5, Date.valueOf(reviewDate));

            statement.executeUpdate();
        }
    }

    /* Prüft, ob ein Produkt mit der gegebenen ID existiert
    @param productId die Produkt-ID
    @return true, wenn das Produkt existiert, sonst false
     */
    private boolean productExists(String productId) throws SQLException {
        String sql = """
                SELECT 1
                FROM product
                WHERE product_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

<<<<<<< HEAD
    /*Diese Methode sucht zuerst, ob der Kunde bereits existiert. Falls nicht, wird er neu angelegt. */
=======
    //Sucht 
>>>>>>> 952fc08b77bfebb8a58136d520b625ee04d94820
    private int findOrCreateCustomer(String customerName) throws SQLException {
        Integer existingCustomerId = findCustomerId(customerName);

        if (existingCustomerId != null) {
            return existingCustomerId;
        }

        String sql = """
                INSERT INTO customer (customer_name)
                VALUES (?)
                RETURNING customer_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("customer_id");
                }
            }
        }

        throw new SQLException("customer_id konnte für customer_name nicht erzeugt werden: " + customerName);
    }

    //Diese Methode sucht eine vorhandene Kundennummer anhand des Namens.
    private Integer findCustomerId(String customerName) throws SQLException {
        String sql = """
                SELECT customer_id
                FROM customer
                WHERE customer_name = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("customer_id");
                }
            }
        }

        return null;
    }

    private String buildReviewText(String summary, String content) {
        if (summary == null && content == null) {
            return null;
        }

        if (summary == null) {
            return content;
        }

        if (content == null) {
            return summary;
        }

        return summary + "\n\n" + content;
    }

 private String emptyToNull(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.trim();
    }                                           //habe die Hilfsmethode hier eingefügt, weil die noch bei dir nicht deklariert war. 

}


    
       
