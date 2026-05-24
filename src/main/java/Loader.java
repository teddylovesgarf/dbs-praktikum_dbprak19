import db.DBConnection;
import error.ErrorLogger;
import java.sql.Connection;
import java.sql.SQLException;
import loader.CategoryLoader;
import loader.ProductLoader;
import loader.ReviewLoader;

public class Loader {

    public static void main(String[] args) {
        // 1. Fehlerprotokoll zurücksetzen und bereitmachen
        ErrorLogger.init();
        
        System.out.println(">>> Starte den Daten-Importvorgang... <<<");

        // 2. Verbindung über try-with-resources öffnen (schließt sich am Ende automatisch!)
        try (Connection conn = DBConnection.getConnection()) {
            
            // 3. XML-Daten für Leipzig und Dresden laden (ProductLoader erledigt StoreLoader mit!)
            System.out.println("Lade Produktdaten (Leipzig & Dresden)...");
            ProductLoader.load("leipzig_transformed.xml", conn);
            ProductLoader.load("dresden.xml", conn);

            // 4. Kategorienbaum einlesen
            System.out.println("Lade Kategoriestruktur...");
            CategoryLoader.load("categories.xml", conn);
            System.out.println("Arbeitsverzeichnis: " + System.getProperty("user.dir"));

            // 5. CSV-Reviews einlesen (Objekt erzeugen, da kein statischer Aufruf)
            System.out.println("Lade Kunden-Reviews...");
            ReviewLoader reviewLoader = new ReviewLoader(conn);
            reviewLoader.loadData("reviews.csv");

            System.out.println(">>> Importvorgang beendet! <<<");

        } catch (SQLException e) {
            System.err.println("KRITISCHER DATENBANKFEHLER in der Main-Steuerung: " + e.getMessage());
        } finally {
            // 6. Am Ende die Statistik auf der Konsole ausgeben
            ErrorLogger.printSummary();
        }
    }
}