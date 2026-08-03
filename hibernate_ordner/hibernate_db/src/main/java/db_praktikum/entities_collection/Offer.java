package db_praktikum.entities_collection;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "offer")
@IdClass(OfferId.class)
public class Offer {     // composite primary key OfferId 

@Column(name = "price")
private BigDecimal price;

@Id
@Column(name = "condition")
private String condition;

@Column(name = "currency")
private String currency;

@Id
@ManyToOne
@JoinColumn(name = "product_id")
private Product product;

@Id
@ManyToOne
@JoinColumn(name = "store_id")
private Store store;


public Offer () {
        
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
