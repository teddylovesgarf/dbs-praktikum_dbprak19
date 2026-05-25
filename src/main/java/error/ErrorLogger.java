package error;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/*Aufgabestellung: ERROR: Entityname (Stadt,Land,Produkt,…), Name des fehlerhaften Attributs, System- oder selbst formulierte Fehlermeldung
Dieses Fehlerprotokoll soll durch Ihr Ladeprogramm automatisch erzeugt werden. Wie viele Fehler welcher Art gab es jeweils? */
/*
 * ErrorLogger protokolliert abgelehnte Datensätze.
 *
 * Fehlerhafte Datensätze werden:
 * 1. auf der Konsole ausgegeben
 * 2. in eine Textdatei geschrieben
 * 3. für eine spätere Zusammenfassung gezählt
 */
public class ErrorLogger {

    private static final String FILE_PATH = "Fehlerprotokoll.txt";

    private static int totalRejected = 0;
    private static int totalLoaded = 0;

    private static final Map<String, Integer> errorCategoryCounts = new HashMap<>();

    /*  init()      // einmal am Anfang, Datei wird neu erstellt
        logError()  // bei jedem Fehler, Fehler wird angehängt
    */
        public static void init() {
        totalRejected = 0;
        totalLoaded = 0;
        errorCategoryCounts.clear();

        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, false))) {
            out.println("=== Fehlerprotokoll abgelehnter Datensätze ===");
        } catch (IOException e) {
            System.err.println("Fehler beim Initialisieren des Fehlerprotokolls: " + e.getMessage());
        }
    } 

    /**
     * Protokolliert einen abgelehnten Datensatz.
     *
     * @param sourceFile Datei, aus der der Datensatz stammt
     * @param lineNumber Zeilennummer in der Quelldatei
     * @param entityType betroffene Entität, zum Beispiel review, store oder product
     * @param attributeName Name des fehlerhaften Attributs, zum Beispiel rating, price oder city
     * @param rawRecord ursprünglicher Rohdatensatz
     * @param errorCategory Fehlerart, zum Beispiel Syntaxfehler, NULL-Fehler, Duplikat
     * @param reason konkrete Begründung der Ablehnung
     */
    public static void logError(
            String sourceFile,
            int lineNumber,
            String entityType,
            String attributeName,
            String rawRecord,
            String errorCategory,
            String reason
    ) {
        totalRejected++;
        //wichtig für PrintSummary(): Fehlerkategorie wird gezählt, damit am Ende die Anzahl pro Kategorie ausgegeben werden kann
        errorCategoryCounts.put(
                errorCategory,
                errorCategoryCounts.getOrDefault(errorCategory, 0) + 1
        );

        String logMessage = String.format(
                "ERROR | Datei: %s | Zeile: %d | Attribut: %s | Entität: %s | Fehlerart: %s | Grund: %s | Datensatz: %s",
                sourceFile,
                lineNumber,
                attributeName,
                entityType,
                errorCategory,
                reason,
                rawRecord
        );

        System.err.println(logMessage);
        //append true: Wenn die Datei bereits existiert, wird der neue Fehler am Ende der Datei angehängt, anstatt sie zu überschreiben.      
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            out.println(logMessage);
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben ins Fehlerprotokoll: " + e.getMessage());
        }
    }

    
     //Zählt einen erfolgreich geladenen Datensatz.
    public static void incrementLoaded() {
        totalLoaded++;
    }

    // Gibt am Ende des Ladeprogramms eine Zusammenfassung aus.
        public static void printSummary() {
        System.out.println();
        System.out.println("=== Import Zusammenfassung ===");

        for (Map.Entry<String, Integer> entry : errorCategoryCounts.entrySet()) {
            System.out.printf("%-25s %d%n", entry.getKey() + ":", entry.getValue());
        }

        System.out.println("-----------------------------");
        System.out.println("Gesamt abgelehnt: " + totalRejected);
        System.out.println("Gesamt geladen:   " + totalLoaded);
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
        out.println();
        out.println("=== Import Zusammenfassung ===");

        for (Map.Entry<String, Integer> entry : errorCategoryCounts.entrySet()) {
            out.printf("%-25s %d%n", entry.getKey() + ":", entry.getValue());
        }

        out.println("-----------------------------");
        out.println("Gesamt abgelehnt: " + totalRejected);
        out.println("Gesamt geladen:   " + totalLoaded);

    } catch (IOException e) {
        System.err.println("Fehler beim Schreiben der Zusammenfassung ins Fehlerprotokoll: " + e.getMessage());
    }
    }
}