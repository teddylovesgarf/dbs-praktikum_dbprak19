package db_praktikum.entities_collection;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OfferId implements Serializable {

   
    @Column(name = "product_id", length = 50,  nullable = false)
    private String productId;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "condition") 
    private String condition; 

    public OfferId() {
    } 

    public OfferId(String productId, Integer storeId, String condition) {
        this.productId = productId;
        this.storeId = storeId;
        this.condition = condition; 


    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof OfferId)) return false;
        OfferId that = (OfferId) o;
        return Objects.equals(productId, that.productId)
        && Objects.equals(storeId, that.storeId)
        && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, storeId, condition);
    }
}
