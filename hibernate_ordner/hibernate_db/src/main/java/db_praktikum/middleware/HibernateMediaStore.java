package db_praktikum.middleware;

import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import db_praktikum.schnittstelle.StoreInterface;
import db_praktikum.entities_collection.*;

public class HibernateMediaStore implements StoreInterface {

    private SessionFactory sessionFactory;

    @Override
    public void init(Properties properties) {
        try {
            Configuration configuration = new Configuration();{
        
                //Automatisch aus hibernate.properties laden
                configuration.configure("hibernate.properties");
            }

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

            // SessionFactory bauen
            sessionFactory = configuration.buildSessionFactory();

            System.out.println("Hibernate initialisiert - SessionFactory aufgebaut");
            System.out.println("  DB: " + configuration.getProperty("hibernate.connection.url"));

        } catch (Exception e) {
            System.err.println("!!! Fehler bei Hibernate-Initialisierung:");
            e.printStackTrace();
            throw new RuntimeException("Hibernate konnte nicht initialisiert werden", e);
        }
    }

    @Override
    public void finish() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("Hibernate beendet");
        }
    }

    // Getter für SessionFactory (brauchst du für CRUD-Operationen!)
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory nicht initialisiert. init() aufrufen!");
        }
        return sessionFactory;
    }

    @Override
    public Product getProduct(String id) {
        return null;
    }

    // ... Rest der Methoden
}