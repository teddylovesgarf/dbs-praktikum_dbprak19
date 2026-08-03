package db_praktikum.entities_collection;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "dvd")
@PrimaryKeyJoinColumn(name = "product_id")
@DiscriminatorValue("dvd")
public class Dvd extends Product {

    @Column(name = "format", length = 100)
    private String format;

    @Column(name = "runtime_minutes")
    private Integer runtimeMinutes;

    @Column(name = "region_code")
    private Integer regionCode;

    public Dvd() {
    }

    public Dvd(String productId, String title, String productType, Integer salesrank, 
        String picture, String format, Integer runtimeMinutes, Integer regionCode) {
        super(productId, title, productType, salesrank, picture);
        this.format = format;
        this.runtimeMinutes = runtimeMinutes;
        this.regionCode = regionCode;
    }

    public String getFormat() {
        return format;
    }

    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setRuntimeMinutes(Integer runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
    }

    public void setRegionCode(Integer regionCode) {
        this.regionCode = regionCode;
    }

    @Override
      public String toString() {
        return "Dvd{" +
                "productId='" + getProductId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", productType='" + getProductType() + '\'' +
                ", salesrank=" + getSalesrank() +
                ", picture='" + getPicture() + '\'' +
                ", format='" + format + '\'' +
                ", runtimeMinutes=" + runtimeMinutes +
                ", regionCode=" + regionCode +
                '}';
      }

}
