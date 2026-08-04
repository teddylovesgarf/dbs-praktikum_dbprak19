package db_praktikum.entities_collection;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "cd_title")
public class CDTitle {

@EmbeddedId
private CDTitleId id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)

    private MusikCD cd;

    //title_number ist in CDTitleId enthalten, daher hier nicht nochmal als Attribut

    @Column(name = "title_name", length = 300, nullable = false)
    private String titleName;

    public CDTitle() {
    }

    public CDTitleId getId(){
        return id;
    }

    public void setId(CDTitleId id) {
        this.id = id;
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