/*Hat der Datensatz 7 Spalten?
Ist product_id vorhanden?
Ist rating vorhanden?
Ist rating eine Zahl?
Liegt rating zwischen 1 und 5?
Ist reviewdate vorhanden?
Ist reviewdate ein gültiges Datum?
Ist user vorhanden?
Existiert das Produkt bereits in der Tabelle product? */

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


public class ReviewLoader {

    private final Connection connection;
    //Verbindung zur Datenbank wird im Konstruktor übergeben, damit sie von loadData() und insertReview() genutzt werden kann
    //@param connection aktive Verbindung zur Datenbank
    public ReviewLoader(Connection connection) {
        this.connection = connection;
    }

//CSV Datei einlesen mit CSVParser
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
                        filePath,
                        lineNumber,
                        "review",
                        "unbekannt",
                        String.join(",", row),
                        "Validierungsfehler",
                        e.getMessage()
                );

            } catch (SQLException e) {
                ErrorLogger.logError(
                        filePath,
                        lineNumber,
                        "review",
                        "Datenbank",
                        String.join(",", row),
                        "Datenbankfehler",
                        "Message: " + e.getMessage()
                                + " | SQLState: " + e.getSQLState()
                                + " | ErrorCode: " + e.getErrorCode()
                );
            }

            lineNumber++;
        }

    } catch (IOException e) {
        ErrorLogger.logError(
                filePath,
                0,
                "review",
                "Datei",
                "",
                "Dateifehler",
                "Datei konnte nicht gelesen werden: " + e.getMessage()
        );
    }
}
    /*Datensätze prüfen
     Jeder Review Datensatz wird geprüft.
     Hat der Datensatz genug Spalten?
     Ist rating eine Zahl?
     Liegt rating zwischen 1 und 5?
     Ist reviewdate ein gültiges Datum?
     Existiert das Produkt schon in der Datenbank?*/
     // @param row eine Zeile aus der CSV Datei
    private void validateReview(String[] row) {
        if (row.length < 7) {
            throw new IllegalArgumentException(
                    "Zu wenige Spalten für review. Erwartet: product, rating, helpful, reviewdate, user, summary, content" //Im ErrorLogger werden sieben Argumente übergeben, deshalb hier sieben Spalten erwartet
            );
        }

        String productId = row[0];
        String ratingRaw = row[1];
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
    //Fehlerhafte Datensätze werden nicht in die Datenbank eingefügt, sondern mit logError() im Fehlerprotokoll festgehalten. Alle Fehler werden gezählt, damit am Ende eine Zusammenfassung der Fehlerkategorien ausgegeben werden kann.
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
    //Prüft, ob ein Produkt mit der gegebenen product_id bereits in der Datenbank existiert. Wenn nicht, wird eine IllegalArgumentException ausgelöst, die im Fehlerprotokoll festgehalten wird.
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
    //Sucht in der Tabelle customer nach einem Eintrag mit dem gegebenen customer_name. Wenn ein Eintrag gefunden wird, wird die zugehörige customer_id zurückgegeben. 
    // Wenn kein Eintrag gefunden wird, wird ein neuer Eintrag mit dem gegebenen customer_name erstellt und die neu generierte customer_id zurückgegeben. Sollte die Erstellung des neuen Eintrags fehlschlagen, wird eine SQLException ausgelöst.
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

    //Sucht die customer_id für den gegebenen customer_name. Wenn kein Eintrag gefunden wird, wird null zurückgegeben.
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

    //Aus summary und content wird der review_text zusammengesetzt. Wenn beide null oder leer sind, wird null zurückgegeben. Wenn nur eines von beiden vorhanden ist, wird dieses zurückgegeben. 
    // Wenn beide vorhanden sind, werden sie mit einem Zeilenumbruch getrennt zusammengefügt.
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
}

    //Hilfsmethode, die leere Strings in null umwandelt. Wenn der übergebene String null oder nur aus Leerzeichen besteht, wird null zurückgegeben. Ansonsten wird der String getrimmt zurückgegeben.
    /*private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }*/
    