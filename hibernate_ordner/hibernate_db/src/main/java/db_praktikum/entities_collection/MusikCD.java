package db_praktikum.entities_collection;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "music_cd")
@DiscriminatorValue("music_cd")
public class MusikCD extends Product {

    @Column(name = "label")
    private String label;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @OneToMany(mappedBy = "cd")
    private List<CDTitle> titles;

    public MusikCD() {
        super();
    }

     public MusikCD(String productId, String title,
            Integer salesrank, String picture,
            String label, LocalDate publicationDate, List<CDTitle> titles
            ) {

        super(productId, title, salesrank, picture);
        this.label = label;
        this.publicationDate = publicationDate;
        this.titles = titles;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<CDTitle> getTitles() {
        return titles;
    }

    public void setTitles(List<CDTitle> titles) {
        this.titles = titles;
    }
    
    @Override
      public String toString() {
        return "MusikCD{" +
                "productId='" + getProductId() + '\'' +
                ", productType='" + getClass().getSimpleName() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", salesrank=" + getSalesrank() +
                ", picture='" + getPicture() + '\'' +
                ", label='" + label + '\'' +
                ", publication date=" + publicationDate +
                //", titles=" + titles +
                //muss raus, da die Beziehung bei der Methode 
                // getProduct(String productId); probleme macht

                '}';
      }

}


