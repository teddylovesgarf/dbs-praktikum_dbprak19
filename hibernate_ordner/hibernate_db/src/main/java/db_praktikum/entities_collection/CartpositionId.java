package db_praktikum.entities_collection;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable

public class CartpositionId implements Serializable{
    @Column(name = "cart_id", nullable = false)
    private Integer cartId;

    @Column(name = "product_id", length = 50, nullable = false)
    private String productId;

    @Column(name = "condition", length = 100, nullable = false)
    private String condition;

    public CartpositionId() {
    }

    public CartpositionId(Integer cartId, String productId, String condition) {
        this.cartId = cartId;
        this.productId = productId;
        this.condition = condition;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartpositionId)) return false;
        CartpositionId that = (CartpositionId) o;
        return Objects.equals(cartId, that.cartId)
                && Objects.equals(productId, that.productId)
                && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId, condition);
    }
}