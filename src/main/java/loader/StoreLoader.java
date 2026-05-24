package loader;

import error.ErrorLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.XMLParser;


/*Ablauf
1.XLM Datei parsen
2. Root Elemant <shop> holen -> Daraus werden die Attribute name, street und zip gelesen.
3. Attribute name, street, zip lesen -> z.B. <shop name="Dresden" street="Johann-Meyer-Straße" zip="01097">
4. Daten prüfen
5. Store in die Datenabnk schreiben
6. Bei Fehlern: Fehlerprotokollierung mit ErrorLogger.logError() 
*/

public class StoreLoader {

    private final Connection connection;
    //Verbindung zur Datenbank wird im Konstruktor übergeben, damit sie von loadData() und insertReview() genutzt werden kann
    //@param connection aktive Verbindung zur Datenbank
    public StoreLoader(Connection connection) {
        this.connection = connection;
    }

    public void loadData(String filePath) {
        try {
            Document doc = XMLParser.parse(filePath);

            if (doc == null) {
                throw new IllegalArgumentException("XML Datei konnte nicht gelesen werden");
            }

            Element shop = XMLParser.getRoot(doc);

            String storeName = XMLParser.getAttribute(shop, "name");
            String street = XMLParser.getAttribute(shop, "street");
            String zip = XMLParser.getAttribute(shop, "zip");

            validateStore(storeName, street, zip);
            insertStore(storeName, street, zip);

            ErrorLogger.incrementLoaded();

        } catch (IllegalArgumentException e) {
            ErrorLogger.logError(
                    filePath,
                    0,
                    "store",
                    "unbekannt",
                    "",
                    "Validierungsfehler",
                    e.getMessage()
            );

        } catch (SQLException e) {
            ErrorLogger.logError(
                    filePath,
                    0,
                    "store",
                    "Datenbank",
                    "",
                    "Datenbankfehler",
                    "Message: " + e.getMessage()
                            + " | SQLState: " + e.getSQLState()
                            + " | ErrorCode: " + e.getErrorCode()
            );
        }
    }

    /*Prüft die Validität eines Store-Objekts anhand der folgenden Kriterien:
    name darf nicht leer sein
    street darf nicht leer sein
    zip darf nicht leer sein 
    zip darf höchsten 10 Zeichen haben und muss eine gültige Postleitzahl sein
    */
    private void validateStore(String storeName, String street, String zip) {
        if (storeName == null || storeName.isBlank()) {
            throw new IllegalArgumentException("store_name fehlt");
        }

        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("street darf nicht NULL oder leer sein");
        }

        if (zip == null || zip.isBlank()) {
            throw new IllegalArgumentException("zip darf nicht NULL oder leer sein");
        }

        if (zip.length() > 10) {
            throw new IllegalArgumentException("zip ist länger als 10 Zeichen");
        }

        if (!zip.matches("\\d+")) {
            throw new IllegalArgumentException("zip enthält nicht nur Ziffern");
        }
    }

        //Fügt einen neuen Store-Datensatz in die Datenbank ein. Wenn die Einfügung fehlschlägt, wird eine SQLException ausgelöst, die im Fehlerprotokoll festgehalten wird.
    private void insertStore(String storeName, String street, String zip) throws SQLException {
        String sql = """
                INSERT INTO store (store_name, street, zip)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, storeName);
            statement.setString(2, emptyToNull(street));
            statement.setString(3, emptyToNull(zip));

            statement.executeUpdate();
        }
    }
    //Macht leere Strings zu null, damit sie als NULL in die Datenbank geschrieben werden. Wenn der übergebene String null oder nur aus Leerzeichen besteht, wird null zurückgegeben. Ansonsten wird der String getrimmt zurückgegeben.
    /*private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
        */

private String emptyToNull(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.trim();
    }

}