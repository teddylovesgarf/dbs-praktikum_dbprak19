package db_praktikum;


import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import db_praktikum.entities_collection.Category;
import db_praktikum.entities_collection.Customer;
import db_praktikum.entities_collection.Offer;
import db_praktikum.entities_collection.Product;
import db_praktikum.middleware.HibernateMediaStore;
import db_praktikum.schnittstelle.StoreInterface;


public class Main {
    private static StoreInterface mediaStore;
    private static Scanner scanner;

    public static void main(String[] args) {
        Properties properties = new Properties();
        mediaStore = new HibernateMediaStore();
        scanner = new Scanner(System.in);

        try (InputStream input = Main.class.getClassLoader()
                .getResourceAsStream("hibernate.properties")) {

            if (input == null) {
                throw new RuntimeException("hibernate.properties wurde nicht gefunden.");
            }

            properties.load(input);

            mediaStore.init(properties);

            System.out.println("Anwendung gestartet.");

            showMainMenu();

        } catch (Exception e) {
            System.err.println("Fehler beim Starten: " + e.getMessage());

            e.printStackTrace();
        } finally {
            try {
                mediaStore.finish();
                System.out.println("\n✓ Anwendung beendet.");
            } catch (Exception ignored) {
            }
        }
    }
            
            //Für die Test-Methoden
            //ProductTest(mediaStore);
            //CategoryTreeTest(mediaStore);
            //CategoryPathTest(mediaStore);

                  
                // === HAUPTMENÜ ===
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════╗");
            System.out.println("║     === MediaStore Menue ===     ║");
            System.out.println("╠ ════════════════════════════════ ╣");
            System.out.println("║ 1 - Produkt suchen               ║");
            System.out.println("║ 2 - Produkte nach Titel-Pattern  ║");
            System.out.println("║ 3 - Kategorienbaum anzeigen      ║");
            System.out.println("║ 4 - Produkte nach Kategoriepfad  ║");
            System.out.println("║ 5 - Top-Produkte anzeigen        ║");
            System.out.println("║ 6 - Guenstigere Produkte         ║");
            System.out.println("║ 7 - Bewertung hinzufuegen        ║");
            System.out.println("║ 8 - Trolls anzeigen              ║");
            System.out.println("║ 9 - Angebote anzeigen            ║");
            System.out.println("║ 0 - Beenden                      ║");
            System.out.println("╚ ════════════════════════════════ ╝");
            System.out.print("Auswahl: ");

            String auswahl = scanner.nextLine().trim();

            switch (auswahl) {
                case "1":
                    ProduktSuche();
                    break;
                case "2":
                    ProduktnachTitel();
                    break;
                case "3":
                    kategorienBaumAnzeigen();
                    break;
                case "4":
                    produkteNachKategoriePathSuchen();
                    break;
                case "5":
                    topProdukteAnzeigen();
                    break;
                case "6": 
                    aehnlicheGuentigereProdukteAnzeigen(); 
                    break;
                case "7": 
                    bewertungHinzufuegen(); 
                        break;
                case "8": 
                    trollsAnzeigen(); 
                        break;
                case "9": 
                    angeboteAnzeigen(); 
                        break;
                case "0":
                    System.out.println("\nAuf Wiedersehen!");
                    return;
                default:
                    System.out.println("Ungueltige Eingabe!");
            }
        }
    }

            
    

    //Hilfsmethoden beim Aufrufen aus middleware

public static void ProduktSuche(){
    System.out.println("Suche Detailinformationen zu einem Produkt");

    String productId;

    while(true){
    System.out.print("Bitte Produkt-ID eingeben oder 0 zum Abbrechen: ");
    productId = scanner.nextLine();

    if(productId.equals("0")){
        System.out.println("Produktsuche abgebrochen.");
        return;
    }
    
    if(!productId.isBlank()){          
        break;   
    }

    System.out.println("Keine Produkt-Id eingegeben.");
    
}
    Product product = mediaStore.getProduct(productId);
         
        if(product == null){
            System.out.println("Produkt nicht gefunden :( ");
            return; 
    }

    System.out.println("Produkt gefunden:");
    System.out.println("Produkt-ID: " + product.getProductId());
    System.out.println("Titel: " + product.getTitle());
    //System.out.println("Produkttyp: " + product.getProductType());
    System.out.println("Salesrank: " + product.getSalesrank());
    System.out.println("Durchschnittsbewertung: " + product.getAverageRating());
    System.out.println("Bild: " + product.getPicture()); 
}

public static void ProduktnachTitel (){
    System.out.println("Suche Produkte nach Titeln: ");

    String pattern;

    while(true){
    System.out.print("Bitte Titel eingeben oder 0 zum Abbrechen: ");
    pattern = scanner.nextLine();

    if(pattern.equals("0")){
        System.out.println("Produktsuche abgebrochen.");
        return;
    }

    if(!pattern.isBlank()){
    break;
    }
    System.out.println("Keine Title eingegeben.");
}
    List<Product> products = mediaStore.getProducts(pattern);    
    if(products.isEmpty()){
        System.out.println("Keine Produkte zum Suchbegriff gefunden :(");
            return; 
    }

    for (Product product : products){
    System.out.println(product.getProductId() + " | " + product.getTitle()); 
    }
}

public static void kategorienBaumAnzeigen() {
    System.out.println("Kategorienbaum anzeigen");

    Category root = mediaStore.getCategoryTree();

    if (root == null) {
        System.out.println("Keine Kategorie gefunden.");
        return;
    }

    // gibt die Root-Kategorie aus
    printCategoryTree(root, 0);
}

private static void printCategoryTree(Category category, int level) {
    for (int i = 0; i < level; i++) {
        System.out.print("  ");
    }

    System.out.println(category.getCategoryId() + " | " + category.getCategoryName());

    for (Category subcategory : category.getSubcategories()) {
        // sorgt dafür, dass jede tiefere Ebene weiter eingerückt wird
        printCategoryTree(subcategory, level + 1);
    }
}

public static void produkteNachKategoriePathSuchen(){
       
    System.out.println("Produkte nach Kategoriepfad suchen");
    String inpuString;

    while(true){
    System.out.print("Bitte Kategoriepfad eingeben oder 0 zum Abbrechen: ");
    inpuString = scanner.nextLine();

    if(inpuString.equals("0")){
        System.out.println("Suche abgebrochen.");
        return;
    }

    if(!inpuString.isBlank()){
    break;
    }
    System.out.println("Keine Angaben getätigt.");
}
    List<String> categoryPath = Arrays.asList(inpuString.split("/"));    

    List <Product> products = mediaStore.getProductsByCategoryPath(categoryPath);

    if(products.isEmpty()){
        System.out.println("Keine Produkte zu diesem Kategoriepfad gefunden :(");
            return; 
    }

    for (Product product : products){
    System.out.println(product.getProductId() + " | " + product.getTitle()); 
    }
}



/*========================================================================================================= */

public static void topProdukteAnzeigen() {

    System.out.println("\n=== Top-Produkte ===");

    System.out.print("Wie viele Top-Produkte moechtest du sehen? ");
    String input = scanner.nextLine().trim();

    int k;

    try {
        k = Integer.parseInt(input);
    } catch (NumberFormatException e) {
        System.out.println("Bitte eine gültige Zahl eingeben.");
        return;
    }

    if (k <= 0) {
        System.out.println("Die Anzahl muss größer als 0 sein.");
        return;
    }

    List<Product> products = mediaStore.getTopProducts(k);

    if (products.isEmpty()) {
        System.out.println("Keine Produkte gefunden.");
        return;
    }

    System.out.println("\nTop " + k + " Produkte:");

    int platz = 1;

    for (Product product : products) {
        System.out.println(
                platz + ". " +
                product.getProductId() + " | " +
                product.getTitle() + " | Bewertung: " +
                product.getAverageRating()
        );

        platz++;
    }
}

/*========================================================================================================= */

public static void aehnlicheGuentigereProdukteAnzeigen() {

    System.out.println("\n=== Aehnliche und guenstigere Produkte ===");

    System.out.print("Bitte Produkt-ID eingeben oder 0 zum Abbrechen: ");
    String productId = scanner.nextLine().trim();

    if (productId.equals("0")) {
        System.out.println("Suche abgebrochen.");
        return;
    }

    if (productId.isBlank()) {
        System.out.println("Keine Produkt-ID eingegeben.");
        return;
    }

    List<Product> products =
            mediaStore.getSimilarCheaperProduct(productId);

    if (products.isEmpty()) {
        System.out.println(
            "Keine aehnlichen und guenstigeren Produkte gefunden."
        );
        return;
    }

    System.out.println("\n=== Gefundene Produkte ===");

    for (Product product : products) {

        System.out.println(
            product.getProductId() +
            " | " +
            product.getTitle() +
            " | Bewertung: " +
            product.getAverageRating()
        );
    }
}

/*========================================================================================================= */
public static void bewertungHinzufuegen() { 
    
    System.out.println("\n=== Bewertung hinzufuegen ===");

    // CustomerID
    System.out.print("Customer-ID eingeben oder 0 zum Abbrechen: ");
    String customerInput = scanner.nextLine().trim();

    if (customerInput.equals("0")) {
        System.out.println("Vorgang abgebrochen.");
        return;
    }

    int customerId;

    try {
        customerId = Integer.parseInt(customerInput);
    } catch (NumberFormatException e) {
        System.out.println("Ungültige Customer-ID.");
        return;
    }


    // ProduktID
    System.out.print("Produkt-ID eingeben: ");
    String productId = scanner.nextLine().trim();

    if (productId.isBlank()) {
        System.out.println("Keine Produkt-ID eingegeben.");
        return;
    }


    // bewertung
    System.out.print("Bewertung eingeben (1-5): ");
    String ratingInput = scanner.nextLine().trim();

    int rating;

    try {
        rating = Integer.parseInt(ratingInput);
    } catch (NumberFormatException e) {
        System.out.println("Ungültige Bewertung.");
        return;
    }

    if (rating < 1 || rating > 5) {
        System.out.println("Die Bewertung muss zwischen 1 und 5 liegen.");
        return;
    }


    // helpful
    System.out.print("Helpful-Wert eingeben: ");
    String helpfulInput = scanner.nextLine().trim();

    int helpful;

    try {
        helpful = Integer.parseInt(helpfulInput);
    } catch (NumberFormatException e) {
        System.out.println("Ungültiger Helpful-Wert.");
        return;
    }


    //Zusammenfassung
    System.out.print("Zusammenfassung eingeben: ");
    String summary = scanner.nextLine().trim();


    // Bewertungstext
    System.out.print("Bewertungstext eingeben: ");
    String reviewText = scanner.nextLine().trim();


    mediaStore.addNewReview(
        customerId,
        productId,
        rating,
        helpful,
        summary,
        reviewText
    );

    System.out.println("\nBewertung wurde hinzugefügt.");
}

/*========================================================================================================= */

public static void trollsAnzeigen() {

    System.out.println("\n=== Trolle anzeigen ===");

    System.out.print("Rating-Schwellenwert eingeben: ");

    String input = scanner.nextLine().trim();

    double threshold;

    try {
        threshold = Double.parseDouble(input);
    } catch (NumberFormatException e) {
        System.out.println("Bitte eine gültige Zahl eingeben.");
        return;
    }

    List<Customer> trolls = mediaStore.getTrolls(threshold);

    if (trolls.isEmpty()) {
        System.out.println("Keine Trolle gefunden.");
        return;
    }

    System.out.println("\nGefundene Trolle:");

    for (Customer customer : trolls) {
        System.out.println(customer);
    }
}

/*========================================================================================================= */

public static void angeboteAnzeigen() {

    System.out.println("\n=== Angebote anzeigen ===");

    System.out.print("Bitte Produkt-ID eingeben oder 0 zum Abbrechen: ");
    String productId = scanner.nextLine().trim();

    if (productId.equals("0")) {
        System.out.println("Suche abgebrochen.");
        return;
    }

    if (productId.isBlank()) {
        System.out.println("Keine Produkt-ID eingegeben.");
        return;
    }

    List<Offer> offers = mediaStore.getOffers(productId);

    if (offers.isEmpty()) {
        System.out.println("Keine Angebote für dieses Produkt gefunden.");
        return;
    }

    System.out.println("\n=== Angebote ===");

    for (Offer offer : offers) {
        System.out.println(offer);
    }
}
}



//Testmethoden

//getProducts(String pattern)
// private static void ProductTest(StoreInterface mediaStore) {
//     System.out.println("Test: getProducts(String pattern)");

//     List<Product> products = store.getProducts("V");

//     for (Product product : products) {
//         System.out.println(product.getProductId() + " | " + product.getTitle());
//     }
// }

//getCategoryTree
// private static void CategoryTreeTest(StoreInterface mediaStore) {
//     System.out.println("Test: getCategoryTree()");

//     Category root = mediaStore.getCategoryTree();

//     if (root == null) {
//         System.out.println("Keine Wurzelkategorie gefunden.");
//         return;
//     }

//     printCategoryTree(root, 0);
// }
// private static void printCategoryTree(Category category, int level) {
//     String indentation = "  ".repeat(level);

//     System.out.println(indentation + category.getCategoryId() + " | " + category.getCategoryName());

//     for (Category subcategory : category.getSubcategories()) {
//         printCategoryTree(subcategory, level + 1);
//     }
// }

//getProductsByCategoryPath braucht CategoryTreeTest
// private static void CategoryPathTest(StoreInterface mediaStore){

//     List<String> categoryPath = Arrays.asList("Features", "Alle SACDs");
//     List<Product> products = mediaStore.getProductsByCategoryPath(categoryPath);

//     if(products.isEmpty()){
//         System.out.println("Keine Produkte für diesen Kategoriepfad gefunden");
//         return;
//     }
//     for(Product product : products){
//         System.out.println(product.getProductId() + " | " + product.getTitle());
//     }



// }


