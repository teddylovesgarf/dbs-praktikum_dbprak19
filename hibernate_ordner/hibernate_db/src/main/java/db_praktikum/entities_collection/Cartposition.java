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
@Table(name = "cartposition")
public class Cartposition {

  @EmbeddedId
  private CartpositionId id;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "price_at_purchase", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceAtPurchase;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public Cartposition() {
    }


    public CartpositionId getId() {
        return id;
    }

    public void setId(CartpositionId id) {
        this.id = id;
    }


    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

     public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductId() {
        return id != null ? id.getProductId() : null;
    }

    public String getCondition() {
        return id != null ? id.getCondition() : null;
    }

    public Integer getCartId() {
        return id != null ? id.getCartId() : null;
    }

    @Override
    public String toString() {
        return "Cartposition{" +
                "cartId=" + getCartId() +
                ", productId='" + getProductId() + '\'' +
                ", condition='" + getCondition() + '\'' +
                ", storeId=" + storeId +
                ", priceAtPurchase=" + priceAtPurchase +
                ", quantity=" + quantity +
                '}';
    }

}
