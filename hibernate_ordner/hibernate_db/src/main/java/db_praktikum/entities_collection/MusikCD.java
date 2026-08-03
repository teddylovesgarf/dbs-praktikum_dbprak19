package db_praktikum.entities_collection;
import java.time.LocalDate;
import java.util.List;

@DiscriminatorValue("music_cd")
public class MusikCD extends Product {
    private String label; 
    private LocalDate publicationDate; 
    private List<CDTitle> titles; 

    public MusikCD() { 
        super();
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

}

