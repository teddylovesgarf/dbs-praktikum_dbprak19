package db_praktikum.entities_collection;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type")
        
public abstract class Product {

    @Id
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

    @OneToMany(mappedBy = "product")
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "product_category",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();


    
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

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
    
