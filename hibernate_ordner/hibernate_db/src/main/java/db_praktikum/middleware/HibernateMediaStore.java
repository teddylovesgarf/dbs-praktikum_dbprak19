package db_praktikum.middleware;

import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import db_praktikum.entities_collection.Book;
import db_praktikum.entities_collection.CDTitle;
import db_praktikum.entities_collection.Cart;
import db_praktikum.entities_collection.Cartposition;
import db_praktikum.entities_collection.Category;
import db_praktikum.entities_collection.Contributor;
import db_praktikum.entities_collection.Customer;
import db_praktikum.entities_collection.Dvd;
import db_praktikum.entities_collection.MusikCD;
import db_praktikum.entities_collection.Offer;
import db_praktikum.entities_collection.Product;
import db_praktikum.entities_collection.Publisher;
import db_praktikum.entities_collection.Review;
import db_praktikum.entities_collection.Store;
import db_praktikum.schnittstelle.StoreInterface;

public class HibernateMediaStore implements StoreInterface {

    private SessionFactory sessionFactory;

    @Override
    public void init(Properties properties) {
        try {
            Configuration configuration = new Configuration();
            
            configuration.setProperties(properties);

            // Alle Entities registrieren
            
            configuration.addAnnotatedClass(Product.class);
            configuration.addAnnotatedClass(Book.class);
            configuration.addAnnotatedClass(Dvd.class); 
            configuration.addAnnotatedClass(MusikCD.class);
            configuration.addAnnotatedClass(Customer.class);
            configuration.addAnnotatedClass(Cart.class);
            configuration.addAnnotatedClass(Cartposition.class);
            configuration.addAnnotatedClass(Offer.class);
            configuration.addAnnotatedClass(Store.class);
            configuration.addAnnotatedClass(Review.class);
            configuration.addAnnotatedClass(Category.class);
            configuration.addAnnotatedClass(Contributor.class);
            configuration.addAnnotatedClass(CDTitle.class);
            configuration.addAnnotatedClass(Publisher.class);

            // SessionFactory bauen
            sessionFactory = configuration.buildSessionFactory();

            System.out.println("Hibernate initialisiert - SessionFactory aufgebaut");

        } catch (Exception e) {
            System.err.println("!!! Fehler bei Hibernate-Initialisierung:");
            e.printStackTrace();
            throw new RuntimeException("Hibernate konnte nicht initialisiert werden", e);
        }
    }

    @Override
    public void finish() { 
        //kommt aus StoreInterface
        if (sessionFactory != null && !sessionFactory.isClosed()) { 
            // Überprüfen, ob die SessionFactory existiert und nicht geschlossen ist
            sessionFactory.close();
            System.out.println("Hibernate beendet");
        }
    }

    //// Interne Hilfsmethode: stellt sicher, dass Hibernate initialisiert wurde.
    private  SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory nicht initialisiert. init() aufrufen!");
        }
        return sessionFactory;
    }

    @Override
    public Product getProduct(String productId){
        return  null;
    }

    @Override
    public List<Product> getProducts(String pattern) {
        return null;
    }

    @Override
    public Category getCategoryTree() {
        return null;
    }

    @Override
    public List<Product> getProductsByCategoryPath(List<String> categoryPath) {
        return null;
    }

    @Override
    public List<Product> getTopProducts(int k) {
        return null;
    }

    @Override
    public List<Product> getSimilarCheaperProduct(String productId) {
        return null;
    }

    @Override
    public void addNewReview(Integer customerId, String productId, 
        Integer rating, Integer helpful, String summary, String reviewText) {
    }

    @Override
    public List<Customer> getTrolls(double ratingThreshold) {
        return null;
    }   

    @Override
    public List<Offer> getOffers(String productId) {
        return null;
    }


}