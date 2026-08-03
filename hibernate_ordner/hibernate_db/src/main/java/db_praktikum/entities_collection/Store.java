package db_praktikum.entities_collection;
 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "store")             
public class Store {        

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")               //Field acess anstatt property access
    private Integer storeId; 

    @Column(name = "store_name", nullable = false)
    private String storeName; 

    @Column(name = "street")
    private String street; 

    @Column(name = "zip")
    private String zipcode; 

    public Store(){

    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return "Store{storeId=" + storeId + ", storeName='" + storeName + "', street='" + street + "', zipcode='" + zipcode + "'}";
    }

}
