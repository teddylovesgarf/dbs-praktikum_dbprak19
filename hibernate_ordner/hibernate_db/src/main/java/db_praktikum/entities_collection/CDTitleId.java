package db_praktikum.entities_collection;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class CDTitleId implements Serializable{

    @Column(name = "product_id", length = 50,  nullable = false)
    private String productId;

    @Column(name = "title_number", nullable = false)
    private Integer titleNumber;

    public CDTitleId() {
    }

    public CDTitleId(String productId, Integer titleNumber) {
        this.productId = productId;
        this.titleNumber = titleNumber;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getTitleNumber() {
        return titleNumber;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setTitleNumber(Integer titleNumber) {
        this.titleNumber = titleNumber;
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof CDTitleId)) return false;
        CDTitleId that = (CDTitleId) o;
        return Objects.equals(productId, that.productId) 
        && Objects.equals(titleNumber, that.titleNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, titleNumber);
    }
    


}
