package db_praktikum;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import db_praktikum.entities_collection.Product;
import db_praktikum.middleware.HibernateMediaStore;
import db_praktikum.schnittstelle.StoreInterface;

public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        StoreInterface store = new HibernateMediaStore();

        try (InputStream input = Main.class.getClassLoader()
                .getResourceAsStream("hibernate.properties")) {

            if (input == null) {
                throw new RuntimeException("hibernate.properties wurde nicht gefunden.");
            }

            properties.load(input);

            store.init(properties);

            System.out.println("Anwendung gestartet.");

            ProductTest(store);

            store.finish();

            System.out.println("Anwendung beendet.");

        } catch (Exception e) {
            e.printStackTrace();

            try {
                store.finish();
            } catch (Exception ignored) {
            }
        }
    }
//Testmethoden

//getProducts(String pattern)
private static void ProductTest(StoreInterface store) {
    System.out.println("Test: getProducts(String pattern)");

    List<Product> products = store.getProducts("V");

    for (Product product : products) {
        System.out.println(product.getProductId() + " | " + product.getTitle());
    }
}
}