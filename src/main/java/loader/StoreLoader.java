package loader;

import error.ErrorLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.XMLParser;


/*Ablauf
1.XML Datei parsen
2. Root Element <shop> holen -> Daraus werden die Attribute name, street und zip gelesen.
z.B. <shop name="Dresden" street="Johann-Meyer-Straße" zip="01097">
4. Daten prüfen
5. Store in die Datenbank schreiben
6. Bei Fehlern: Fehlerprotokollierung mit ErrorLogger.logError() 
*/

public class StoreLoader {

    private final Connection connection;
    //Verbindung zur Datenbank wird im Konstruktor übergeben, damit sie von loadData() und insertStore() genutzt werden kann
    //@param connection aktive Verbindung zur Datenbank
    public StoreLoader(Connection connection) {
        this.connection = connection;
    }

    public void loadData(String filePath) {
        try {
            //XML Datei öffnen und als Dokument speichern. Wenn die Datei nicht gelesen werden kann, wird eine IllegalArgumentException ausgelöst, die im Fehlerprotokoll festgehalten wird.
            Document doc = XMLParser.parse(filePath);

            if (doc == null) {
                throw new IllegalArgumentException("XML Datei konnte nicht gelesen werden");
            }
            // Das Root Element ist bei unseren Dateien das <shop> Element
            Element shop = XMLParser.getRoot(doc);

            String storeName = XMLParser.getAttribute(shop, "name");
            String street = XMLParser.getAttribute(shop, "street");
            String zip = XMLParser.getAttribute(shop, "zip");

            

    /*Prüft die Validität eines Store-Objekts anhand der folgenden Kriterien:
    name darf nicht leer sein
    street darf nicht leer sein
    zip darf nicht leer sein 
    zip darf höchsten 10 Zeichen haben und muss nur Ziffern enthalten
    */
    
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


        //Fügt einen neuen Store-Datensatz in die Datenbank ein. Wenn die Einfügung fehlschlägt, wird eine SQLException ausgelöst, die im Fehlerprotokoll festgehalten wird.
        String sql = """
                INSERT INTO store (store_name, street, zip)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, storeName);
            statement.setString(2, street);
            statement.setString(3, zip);

            //Führt da SQL-Statement aus, um den neuen Store-Datensatz in die Datenbank einzufügen.
            statement.executeUpdate();
        }
        //Wenn alles geklappt hat, wird ein geladener Datensatz gezählt, damit am Ende die Anzahl der erfolgreich geladenen Datensätze ausgegeben werden kann.
        ErrorLogger.incrementLoaded();


        }catch (IllegalArgumentException e) {
            //Fehler bei fehlenden oder ungültigen XML Werten
            ErrorLogger.logError(
                filePath,
                0,
                "store",
                "unbekannt",
                "",
                "Validierungsfehler",
                e.getMessage()
            );

        } catch(SQLException e) {
            //Fehler beim Einfügen in die Datenbank
            ErrorLogger.logError(
                filePath,  //Wo ist der Fehler passiert?
                0,  //welche Zeile? Bei XML unbekannt, daher 0
                "store", // Welche Entität? -> store
                "Datenbank", //Welches Attribut? -> Fehler beim Einfügen in die Datenbank
                "", //welche Rohdaten? -> keine, da Fehler beim Einfügen in die Datenbank
                "Datenbankfehler", //welche Art von Fehler? -> Datenbankfehler
                "Message: " + e.getMessage() //genaue Fehlerbeschreibung
                            + " | SQLState: " + e.getSQLState()
                            + " | ErrorCode: " + e.getErrorCode()
            );
        }
    }
}

   
        
