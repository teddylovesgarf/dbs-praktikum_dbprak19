package db_praktikum.entities_collection;

 abstract class Product {

   private String productId; 
    private String title;
   private Integer salesrank;
    private String picture; 
    private double averageRating;

    
    public Product() {
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

    public void setSalesrank(int salesrank) {
        this.salesrank = salesrank;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    
}
    
