package db_praktikum.schnittstelle;

import java.util.Properties;
import java.util.List;

public interface StoreInterface {
    void init(Properties props);
    void finish();
    Product getProduct(int productId);
    List<Product> getProducts(String pattern);
    Category getCategoryTree();
    List<Product> getProductsByCategoryPath(List<String> path);
    List<Product> getTopProducts(int k);
    List<Product> getSimilarCheaperProduct(int productId);
    void addNewReview(int productId, int reviewerId, int rating, String text);
    List<Reviewer> getTrolls(double ratingThreshold);
    List<Offer> getOffers(int productId);
}