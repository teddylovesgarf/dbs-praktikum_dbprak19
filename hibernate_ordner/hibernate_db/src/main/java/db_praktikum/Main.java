package db_praktikum;


import java.io.InputStream;
import java.util.Properties;

import db_praktikum.entities_collection.Category;
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

            //ProductTest(mediaStore);
            CategoryTreeTest(mediaStore);

            mediaStore.finish();

            System.out.println("Anwendung beendet.");

        } catch (Exception e) {
            e.printStackTrace();

            try {
                mediaStore.finish();
            } catch (Exception ignored) {
            }
        }
    }
//Testmethoden

//getProducts(String pattern)
// private static void ProductTest(StoreInterface store) {
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


}