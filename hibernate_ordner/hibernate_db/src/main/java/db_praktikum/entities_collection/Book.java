package db_praktikum.entities_collection;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
@DiscriminatorValue("book")
@PrimaryKeyJoinColumn(name = "product_id")

public class Book extends Product {

    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @Column(name = "pages")
    private Integer pages;

    @Column(name = "publication_date")
    private LocalDate publicationDate;
    
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    public Book(){

    }

    public Book(String productId, String title, String productType,
            Integer salesrank, String picture,
            String isbn, Integer pages,
            LocalDate publicationDate, Publisher publisher) {

        super(productId, title, productType, salesrank, picture);
        this.isbn = isbn;
        this.pages = pages;
        this.publicationDate = publicationDate;
        this.publisher = publisher;
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

    public Publisher getPublisher(){
        return publisher;
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

    public void setPublisher(Publisher publisher){
        this.publisher = publisher;
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
                ", publisher=" + publisher +
                '}';
                
            }

}
