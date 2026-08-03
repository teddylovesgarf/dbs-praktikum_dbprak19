package db_praktikum.entities_collection;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cd_title")
public class CDTitle {
    
@EmbeddedId
@Embedded
    private CDTitle id id;
    private Integer titleNumber;
    private String titleName;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private MusikCD cd;   // referenz zur zugehörigen CD

    public CDTitle() {
    }

    public Integer getTitleNumber() {
        return titleNumber;
    }

    public void setTitleNumber(Integer titleNumber) {
        this.titleNumber = titleNumber;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public MusikCD getCd() {
        return cd;
    }

    public void setCd(MusikCD cd) {
        this.cd = cd;
    }
}