package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
CSVParser liest eine CSV Datei ein und gibt jede Datenzeile als String Array zurück.
Beispiel reviews.csv:
row[0] = product
row[1] = rating
row[2] = helpful
row[3] = reviewdate
row[4] = user
row[5] = summary
row[6] = content

wichtig: 
Kommas innerhalb der Anführungszeichen werden nicht als Trennzeichen erkannt.
 Beispiel:
 "geniale CD, sehr gut" -> wird als ein Feld erkannt, obwohl ein Komma enthalten ist.
 */

public class CSVParser {

    public List<String[]> readCSV(String filePath, char separator) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // Kopfzeile überspringen
            reader.readLine();
            
            
            /*
            Datei zeilenweise lesen.
            Jede Zeile wird mit parseLine in einzelne Spalten zerlegt.
            */
            while ((line = reader.readLine()) != null) {
                String[] columns = parseLine(line, separator);
                rows.add(columns);
            }
        }
        return rows;
    }

    /*
    Zerlegt eine CSV Zeile in einzelne Spalten.
    Berücksichtigt dabei Anführungszeichen, um Trennzeichen innerhalb von Textfeldern zu ignorieren.

    @param line einzelne CSV Zeile
    @param separator Trennzeichen ','
    @return Spaltenwerte der Zeile
     */

    private String[] parseLine(String line, char separator){
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();

         /*
         Merkt, ob der Parser sich gerade innerhalb von Anführungszeichen befindet.
         false: normales Feld
         true: Textfeld innerhalb von "..."
         */
        boolean insideQuotes = false;

        //Jedes Zeichen in der Zeile durchgehen
        for (int i = 0; i < line.length(); i++) {
                char currentChar = line.charAt(i);

            // Anführungszeichen öffnen oder schließen ein Textfeld
            if (currentChar == '"') {
                insideQuotes = !insideQuotes;
            
             // Separator trennt nur, wenn wir nicht innerhalb von Anführungszeichen sind             
            }else if (currentChar == separator && !insideQuotes) {
                values.add(currentValue.toString().trim());
                currentValue.setLength(0); // StringBuilder zurücksetzen

             // Normales Zeichen, wird zum aktuellen Feld hinzugefügt
            } else {
                currentValue.append(currentChar);
            }
        }
        //Letzes Feld hinzufügen.
        values.add(currentValue.toString().trim());

        // Syntaxprüfung: Anführungszeichen müssen geschlossen sein
        if (insideQuotes) {
            throw new IllegalArgumentException("Nicht geschlossene Anführungszeichen in CSV-Zeile");
        }

        return values.toArray(new String[0]);
    }

    /*public static void main(String[] args) {
        CSVParser parser = new CSVParser();
        try {
            List<String[]> rows = parser.readCSV("reviews.csv", ',');
            for (int i=0; i<5 && i< rows.size(); i++) {
                String[] row =rows.get(i);
                
                System.out.println("Produkt: " + row[0] + ", Bewertung: " + row[1] + ", Hilfreich: " + row[2] + ", Datum: " + row[3] + ", Benutzer: " + row[4] + ", Zusammenfassung: " + row[5] + ", Inhalt: " + row[6]);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der CSV-Datei: " + e.getMessage());
        }catch (IllegalArgumentException e) {
            System.err.println("Fehler beim Parsen der CSV-Datei: " + e.getMessage());
        

    }
}*/
}
