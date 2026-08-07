package db_praktikum;


import java.io.InputStream;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import db_praktikum.entities_collection.Category;
import db_praktikum.entities_collection.Product;
import db_praktikum.middleware.HibernateMediaStore;
import db_praktikum.schnittstelle.StoreInterface;

public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        StoreInterface mediaStore = new HibernateMediaStore();

        try (InputStream input = Main.class.getClassLoader()
                .getResourceAsStream("hibernate.properties")) {

            if (input == null) {
                throw new RuntimeException("hibernate.properties wurde nicht gefunden.");
            }

            properties.load(input);

            mediaStore.init(properties);

            System.out.println("Anwendung gestartet.");
            
            //Für die Test-Methoden
            //ProductTest(mediaStore);
            //CategoryTreeTest(mediaStore);
            //CategoryPathTest(mediaStore);

            Scanner scanner = new Scanner(System.in);
            System.out.println();
            System.out.println("=== MediaStore Menü ===");
            System.out.println("1 - Produkt suchen");
            System.out.println("2 - Produkte nach Titel suchen");
            System.out.println("3 - Kategorienbaum anzeigen");
            System.out.println("4 - Produkte nach Kategoriepfad suchen");
            System.out.println("5 - Top-Produkte anzeigen");
            System.out.println("6 - Ähnliche günstigere Produkte anzeigen");
            System.out.println("7 - Bewertung hinzufügen");
            System.out.println("8 - Trolls anzeigen");
            System.out.println("9 - Angebote anzeigen");
            System.out.println("0 - Beenden");
            System.out.print("Auswahl: ");
            String auswahl = scanner.nextLine();

            if(auswahl.equals("1")){
                ProduktSuche(mediaStore, scanner);
                
            }else if (auswahl.equals("2")){
                    System.out.println("Produkte nach Titel suchen gewählt.");

            } else if (auswahl.equals("3")) {
                System.out.println("Kategorienbaum anzeigen gewählt.");

            } else if (auswahl.equals("4")) {
                System.out.println("Produkte nach Kategoriepfad suchen gewählt.");

            } else if (auswahl.equals("0")) {
                System.out.println("Beenden gewählt.");

            } else {
                System.out.println("Ungültige Eingabe.");
            }
                  
            

            
        


            mediaStore.finish();

            System.out.println("Anwendung beendet.");

        } catch (Exception e) {
            e.printStackTrace();

            try {
                mediaStore.finish();
            } catch (Exception ignored)
        }
    }
}
    

    //Hilfsmethoden beim Aufrufen aus middleware

public static void ProduktSuche(StoreInterface mediaStore, Scanner scanner){
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

    System.out.println(product.getProductId() + " | " + product.getTitle()); 
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


}