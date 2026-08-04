package db_praktikum.schnittstelle;

import java.util.List;
import java.util.Properties;

import db_praktikum.entities_collection.Category;
import db_praktikum.entities_collection.Customer;
import db_praktikum.entities_collection.Offer;
import db_praktikum.entities_collection.Product;

public interface StoreInterface {

    void init(Properties properties);

    void finish();

    Product getProduct(String productId);
    List<Product> getProducts(String pattern);
    
    Category getCategoryTree();
/*getCategoryTree
Diese Methode ermittelt den kompletten Kategorienbaum durch Rückgabe des Wurzelknotens. 
Jeder Knoten ist dabei vom Typ Category und kann eine Liste von Unterknoten 
(d.h. Unterkategorien) enthalten */

    List<Product> getProductsByCategoryPath(List<String> categoryPath);
/*getProductsByCategoryPath
Nach Angabe einer Kategorie (definiert durch den Pfad von der Wurzel zu sich selbst) 
soll die Liste der zugeordneten Produkte ermittelt werden. 
Die Angabe des Pfades ist notwendig, 
da der Kategorienname allein nicht eindeutig ist. */

    List<Product> getTopProducts(int k);
/*getTopProducts
Diese Methode liefert eine Liste aller Produkte zurück,
die unter den Top k sind basierend auf dem Rating. */

    List<Product> getSimilarCheaperProduct(String productId);
/*getSimilarCheaperProduct
Diese Methode liefert für ein Produkt(Id) eine List von Produkten, 
die ähnlich und billiger sind als das spezifizierte. */

    void addNewReview(Integer customerId, String productId, Integer rating, 
        Integer helpful, String summary, String reviewText);
/*addNewReview
Die Rahmenapplikation erlaubt sowohl das Ansehen als auch Hinzufügen von Reviews. 
MIt Hilfe der Methode wird ein neues Review in der Datenbank gespeichert. */ 
        
     
    List<Customer> getTrolls(double ratingThreshold);
/*getTrolls
Die Methode soll eine Liste von Nutzern ausgeben, 
deren Durchschnittsbewertung unter einem spezifizierten Rating ist.*/   
    
    List<Offer> getOffers(String productId);
/*getOffers
Für das übergegebene Produkt(Id) werden alle verfügbaren Angebote zurückgeliefert.*/
}