package db_praktikum.entities_collection;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "category_name", length=200, nullable = false)
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> subcategories = new ArrayList<>();

    @ManyToMany(mappedBy ="categories")
    private List<Product> products = new ArrayList<>();

    public Category() {
    }

    public Category(String categoryName, Category parentCategory) {
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public List<Category> getSubcategories() {
        return subcategories;
    }

    public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
}

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public void setSubcategories(List<Category> subcategories) {
        this.subcategories = subcategories;
    }
    //Hoffentlich nützlich bei getProductsByCategoryPath()
    public List<Product> getProducts() {
        return products;
}

    public void setProducts(List<Product> products) {
        this.products = products;
}

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", parentCategory=" + (parentCategory != null ? parentCategory.getCategoryId() : null) +
                '}';
    }

}
