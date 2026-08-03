package db_praktikum.entities_collection;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type")

public abstract class Product {

   private String productId; 
    private String title;
   private Integer salesrank;
    private String picture; 
    private double averageRating;

    
    public Product() {
    }

    protected Product(String productId, String title, Integer salesrank, String picture) {
    this.productId = productId;
    this.title = title;
    this.salesrank = salesrank;
    this.picture = picture;
}
    public String getProductId() { 
        return productId; 
    }

    public String getTitle() {
        return title; 
    }

    public Integer getSalesrank() {
        return salesrank; 
    }
    public String getPicture() {
        return picture; 
    }
    
    public double getAverageRating() {
        return averageRating;
    }


    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSalesrank(Integer salesrank) {
        this.salesrank = salesrank;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    
}
    
