package db_praktikum.middleware;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
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
       try (Session session = getSessionFactory().openSession()) {
            return session.get(Product.class, productId);
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen des Produkts: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Product> getProducts(String pattern) {

        try(Session session = getSessionFactory().openSession()){

            if(pattern == null || pattern.isBlank()){
                return session.createQuery("FROM Product p ORDER BY p.title",
                 Product.class)
                 .list();
            }
            return session.createQuery(
                "FROM Product p WHERE p.title LIKE :pattern ORDER BY p.title",
                Product.class
            )
            .setParameter("pattern", "%" + pattern + "%")
            .list();
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Produkte: " + e.getMessage());
            return List.of();

        }
    }
        

@Override
public Category getCategoryTree() {
    try (Session session = getSessionFactory().openSession()) {
        List<Category> rootCategories = session.createQuery(
                "FROM Category c WHERE c.parentCategory IS NULL ",
                Category.class
        ).list();

        if (rootCategories.isEmpty()) {
            return null;
        }

    
    Category root = rootCategories.get(0);

    loadSubcategories(root);

    return root;
    } catch (Exception e){
        System.err.println("Fehler beim Abrufen des Kategorienbaums: " + e.getMessage());
        return null;
    }
}

private void loadSubcategories(Category category) {
    for (Category sub : category.getSubcategories()) {
        loadSubcategories(sub);
    }
}

    @Override
    public List<Product> getProductsByCategoryPath(List<String> categoryPath) {
        if(categoryPath==null || categoryPath.isEmpty()){
                return List.of();
        }
        Category root = getCategoryTree();

        if(root == null) {
            return List.of();
        }

        if (!categoryPath.get(0).equals(root.getCategoryName())) {
            return List.of(); 
        }
    Category currentCategory = root;

    //Ab i = 1, da das erste Element schon mit der Root verglichen
    for (int i = 1; i < categoryPath.size(); i++) {

        //nächsten Kategorienamen aus dem Pfad
        String nextCategoryName = categoryPath.get(i);

        Category foundCategory = null;

        for (Category subcategory : currentCategory.getSubcategories()) {
            if (nextCategoryName.equals(subcategory.getCategoryName())) {

                //bei passenden Kategorie speichern
                foundCategory = subcategory;
                
                break;
            }
        }
        //Wenn auf dieser Ebene keine passende Kategorie gefunden wurde, ist der Pfad ungültig.
        if (foundCategory == null) {
            return List.of();
        }

        //Die gefundene Unterkategorie wird zur neuen aktuellen Kategorie. 
        //Danach geht die äußere Schleife zum nächsten Pfadelement.
        currentCategory = foundCategory;
    }
    // Nach erfolgreicher Navigation die Produkte der Kategorie aus der DB laden
    try (Session session = getSessionFactory().openSession()) {

        return session.createQuery(
            "SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE c.categoryId = :categoryId ORDER BY p.title",
            Product.class)
            .setParameter("categoryId", currentCategory.getCategoryId())
            .list();

    } catch (Exception e) {
        System.err.println("Fehler beim Abrufen der Produkte nach Kategoriepfad: " + e.getMessage());
        return List.of();
    }
    }

// --------------------------------------------------------------------------------------------------------------
    @Override
    public List<Offer> getOffers(String productId) {

        try(Session session = getSessionFactory().openSession()){

            return session.createQuery("SELECT o FROM Offer o WHERE o.id.productId = :productId ORDER BY o.product.title", 
            Offer.class)
        .setParameter("productId", productId)
        .getResultList();  
    } catch (Exception e) {
        System.err.println("Fehler beim Abrufen der Angebote " + e.getMessage());
        return List.of();
    }
    }


    @Override
    public List<Product> getTopProducts(int k) {
        try(Session session = getSessionFactory().openSession()){

            return session.createQuery("SELECT p FROM Product p WHERE p.averageRating IS NOT NULL ORDER BY p.averageRating DESC", 
            Product.class)
        .setMaxResults(k)    
        .getResultList();   
        } catch (Exception e) {
        System.err.println("Fehler beim Abrufen der Top-Produkte " + e.getMessage());
        return List.of();
    }
    }

    @Override
    public List<Product> getSimilarCheaperProduct(String productId) {
    
        try(Session session = getSessionFactory().openSession()){   
            BigDecimal lowerPrice = session.createQuery(             //günstigster Preis des Ausgangsprodukts
    "SELECT MIN(o.price) FROM Offer o WHERE o.id.productId = :productId", BigDecimal.class)
            .setParameter("productId", productId)
            .getSingleResult();
        
            
        return session.createQuery(                 //ausgehend vom productId, über die similarProducts Beziehung zu sp navigieren, dann deren Angebotspreise gegen  Preiswert aus Schritt 1 prüfen
            "SELECT DISTINCT sp FROM Product p JOIN p.similarProducts sp JOIN Offer o ON o.id.productId = sp.productId" 
            + " WHERE p.productId = :productId AND o.price < :lowerPrice",
     Product.class)
        .setParameter("productId", productId)
        .setParameter("lowerPrice", lowerPrice)
        .getResultList();
         } catch (Exception e) {
        System.err.println("Fehler beim Abrufen der Top-Produkte " + e.getMessage());
        return List.of();
    }}


//persist() für neue Entities, die noch keine ID in der DB haben. macht Objekt dauerhaft

//
    @Override
    public void addNewReview(Integer customerId, String productId, Integer rating, Integer helpful, String summary, String reviewText) {
    try (Session session = getSessionFactory().openSession()) {
        session.beginTransaction();

        Product product = session.get(Product.class, productId);
        Customer customer = session.get(Customer.class, customerId);

        if ((product == null) || (customer == null)) {
            System.out.print("Fehlehrafte Eingabe"); 
            session.getTransaction().rollback();
            return;
        }

        Review review = new Review();
        review.setRating(rating);
        review.setHelpful(helpful);
        review.setSummary(summary);
        review.setReviewText(reviewText);
        review.setProduct(product);
        review.setCustomer(customer);
        review.setReviewDate(LocalDate.now());
        

        session.persist(review);
        session.getTransaction().commit();
    } catch (Exception e) {
        System.err.println("Fehler beim Speichern der Review " + e.getMessage());
    }
}



/*Die Methode soll eine Liste von Nutzern ausgeben, 
deren Durchschnittsbewertung unter einem spezifizierten Rating ist.*/  

    @Override
    public List<Customer> getTrolls(double ratingThreshold) {
        try (Session session = getSessionFactory().openSession()) {

        return session.createQuery("SELECT r.customer FROM Review r  GROUP BY r.customer HAVING AVG(r.rating) < :threshold", 
        Customer.class)
    .setParameter("threshold", ratingThreshold)
    .getResultList();


    }   catch (Exception e) {
        System.err.println(
            "Fehler beim Abrufen der Trolle: " + e.getMessage()
        );
        return List.of();
    }
}

}