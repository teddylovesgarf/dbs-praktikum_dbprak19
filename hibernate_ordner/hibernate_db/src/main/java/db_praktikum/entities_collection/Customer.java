package db_praktikum.entities_collection;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity 
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false) 
    private Integer customerId;

    @Column(name = "customer_name", length=50, nullable = false, unique = true)
    private String customerName;

    @OneToMany(mappedBy = "customer")
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Review> reviews = new ArrayList<>();

    public Customer() {
    }

    public Customer(String customerName) {
        this.customerName = customerName;
    }
    public Integer getCustomerId() {
        return customerId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public List<Cart> getCarts() {
        return carts;
    }
    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }
    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Override  
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                '}';
    }

}
