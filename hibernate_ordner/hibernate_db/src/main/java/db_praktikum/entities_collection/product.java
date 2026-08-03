package db_praktikum.entities_collection;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
@DiscriminatorColumn(name = "product_type")
        
public abstract class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")   
   private String productId; 

   @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "salesrank")
   private Integer salesrank;

   @Column(name = "picture")
    private String picture; 

    @Column(name = "avg_rating_product")
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
    
