package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVParser liest eine CSV Datei ein und gibt jede Datenzeile
 * als String Array zurück.
 *
 * Wichtig:
 * Diese Klasse berücksichtigt Anführungszeichen.
 * Dadurch werden Trennzeichen innerhalb von Textfeldern nicht falsch getrennt.
 *
 * Beispiel:
 * "geniale CD, sehr gut"
 * wird als ein Feld erkannt, obwohl ein Komma enthalten ist.
 */

public class CSVParser {

    public List<String[]> readCSV(String filePath, char separator) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // Kopfzeile überspringen
            reader.readLine();
            
            /*Datei zeilenweise lesen.
             * Jede Zeile wird mit parseLine in einzelne Spalten zerlegt.*/
            while ((line = reader.readLine()) != null) {
                String[] columns = parseLine(line, separator);
                rows.add(columns);
            }
        }
        return rows;
    }

    /*Zerlegt eine CSV Zeile in einzelne Spalten.
    * Berücksichtigt dabei Anführungszeichen, um Trennzeichen innerhalb von Textfeldern zu ignorieren.
    * @param line einzelne CSV Zeile
    * @param separator Trennzeichen, zum Beispiel ','
    * @return Spaltenwerte der Zeile
     */

    private String[] parseLine(String line, char separator){
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();

         /*
         * Merkt, ob der Parser sich gerade innerhalb von Anführungszeichen befindet.
         * false: normales Feld
         * true: Textfeld innerhalb von "..."
         */
        boolean insideQuotes = false;

        /* Jedes Zeichen in der Zeile durchgehen */
        for (int i = 0; i < line.length(); i++) {
                char currentChar = line.charAt(i);


            if (currentChar == '"') {
                insideQuotes = !insideQuotes;
            /*
             * Wenn das aktuelle Zeichen das Trennzeichen ist
             * und wir uns NICHT innerhalb von Anführungszeichen befinden,
             * ist das aktuelle Feld beendet.
             */
            } else if (currentChar == separator && !insideQuotes) {
                values.add(currentValue.toString().trim());
                currentValue.setLength(0);

                // Normales Zeichen, wird zum aktuellen Feld hinzugefügt
            } else {
                currentValue.append(currentChar);
            }
        }
        values.add(currentValue.toString().trim());
        return values.toArray(new String[0]);
    }
}
