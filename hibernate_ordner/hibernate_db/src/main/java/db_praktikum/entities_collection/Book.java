package db_praktikum.entities_collection;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
@PrimaryKeyJoinColumn(name = "product_id")

public class Book extends Product {

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "pages")
    private Integer pages;

    @Column(name = "publication_date")
    private LocalDate publicationDate;
    
    @Column(name = "publisher_id")
    private Integer publisherId;

    public Book(){

    }

    public Book(String productId, String title, String productType,
            Integer salesrank, String picture,
            String isbn, Integer pages,
            LocalDate publicationDate, Integer publisherId) {

        super(productId, title, productType, salesrank, picture);
        this.isbn = isbn;
        this.pages = pages;
        this.publicationDate = publicationDate;
        this.publisherId = publisherId;
    }

    public String getIsbn(){
        return isbn;
    }

    public Integer getPages(){
        return pages;
    }

    public LocalDate getPublicationDate(){
        return publicationDate;
    }

    public Integer getPublisherId(){
        return publisherId;
    }

    public void setIsbn(String isbn){
        this.isbn = isbn;
    }

    public void setPages(Integer pages){
        this.pages = pages;
    }

    public void setPublicationDate(LocalDate publicationDate){
        this.publicationDate = publicationDate;
    }

    public void setPublisherId(Integer publisherId){
        this.publisherId = publisherId;
    }

    @Override
    public String toString() {
        return "Book{" +
                "productId='" + getProductId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", productType='" + getProductType() + '\'' +
                ", salesrank=" + getSalesrank() +
                ", picture='" + getPicture() + '\'' +
                ", isbn='" + isbn + '\'' +
                ", pages=" + pages +
                ", publicationDate=" + publicationDate +
                ", publisherId=" + publisherId +
                '}';
                
            }

}
