package db_praktikum.entities_collection;

import java.time.LocalDate;

public class Review {
    private Integer reviewId;
    private Integer rating; 
    private Integer helpful; 
    private String summary;
    private String reviewText; 
    private LocalDate reviewDate;
    private Product product;
    private Customer customer;

    public Review() {
    
    }

    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public Integer getHelpful() {
        return helpful;
    }
    public void setHelpful(Integer helpful) {
        this.helpful = helpful;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getReviewText() {
        return reviewText;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
    public LocalDate getReviewDate() {
        return reviewDate;
    }
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    } 

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


}
