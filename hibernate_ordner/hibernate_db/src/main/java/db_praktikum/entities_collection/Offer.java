package db_praktikum.entities_collection;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;



@Entity
@Table(name = "offer")
public class Offer {     // composite primary key OfferId 


@EmbeddedId 
private OfferId id; 

@Column(name = "price")
private BigDecimal price;

@Column(name = "currency")
private String currency;


@ManyToOne
@MapsId("productId")
@JoinColumn(name = "product_id")
private Product product;


@ManyToOne
@MapsId("storeId")
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

    public OfferId getId() {
        return id;
    }

    public void setId(OfferId id) {
        this.id = id;
    }
}
