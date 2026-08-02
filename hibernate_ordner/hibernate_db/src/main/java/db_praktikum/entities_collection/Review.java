package db_praktikum.entities_collection;

import java.time.LocalDate;

public class Review {
    private Integer rating; 
    private Integer helpful; 
    private String summary;
    private String reviewText; 
    private LocalDate reviewDate;

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


}
